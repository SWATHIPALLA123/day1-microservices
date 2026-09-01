package com.example.orderservice.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.orderservice.dto.OrderData;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.UserResponse;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;

@Service
public class OrderService {

    private final RestClient restClient;

    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    private final Retry retry;

    private final TimeLimiter timeLimiter;

    private final RateLimiter rateLimiter;

    private final Bulkhead bulkhead;


    private final ExecutorService executorService =
            Executors.newCachedThreadPool();


    private final Map<Long, OrderData> orders = Map.of(

            101L,
            new OrderData(101L, 1L, "Laptop", 1),

            102L,
            new OrderData(102L, 2L, "Keyboard", 2),

            103L,
            new OrderData(103L, 3L, "Mouse", 1)
    );


    public OrderService(

            @Value("${user-service.base-url}")
            String userServiceBaseUrl,

            CircuitBreakerFactory<?, ?> circuitBreakerFactory,

            RetryRegistry retryRegistry,

            TimeLimiterRegistry timeLimiterRegistry,

            RateLimiterRegistry rateLimiterRegistry,

            BulkheadRegistry bulkheadRegistry
    ) {

        this.restClient = RestClient.builder()
                .baseUrl(userServiceBaseUrl)
                .build();


        this.circuitBreakerFactory =
                circuitBreakerFactory;


        this.retry =
                retryRegistry.retry("userService");


        this.timeLimiter =
                timeLimiterRegistry.timeLimiter("userService");


        this.rateLimiter =
                rateLimiterRegistry.rateLimiter("userService");


        this.bulkhead =
                bulkheadRegistry.bulkhead("userService");


        // ===============================
        // Retry Logging
        // ===============================

        this.retry.getEventPublisher()
                .onRetry(event ->
                        System.out.println(
                                "Retry attempt: "
                                        + event.getNumberOfRetryAttempts()
                                        + " for User Service"
                        )
                );


        // ===============================
        // Rate Limiter Logging
        // ===============================

        this.rateLimiter.getEventPublisher()

                .onSuccess(event ->
                        System.out.println(
                                "Rate Limiter: Request permitted"
                        )
                )

                .onFailure(event ->
                        System.out.println(
                                "Rate Limiter: Request rejected"
                        )
                );


        // ===============================
        // Bulkhead Logging
        // ===============================

        this.bulkhead.getEventPublisher()

                .onCallPermitted(event ->
                        System.out.println(
                                "Bulkhead: Request permitted"
                        )
                )

                .onCallRejected(event ->
                        System.out.println(
                                "Bulkhead: Request rejected"
                        )
                );
    }


    // =================================
    // Get Order By ID
    // =================================

    public OrderResponse getOrderById(Long orderId) {

        // Find Order
        OrderData order = orders.get(orderId);


        // Check whether order exists
        if (order == null) {

            throw new OrderNotFoundException(
                    "Order not found with id: "
                            + orderId
            );
        }


        UserResponse user;


        try {

            /*
             * Request Flow
             *
             * Rate Limiter
             *       ↓
             * Bulkhead
             *       ↓
             * Retry
             *       ↓
             * Circuit Breaker
             *       ↓
             * Time Limiter
             *       ↓
             * User Service
             */

            user = RateLimiter.decorateSupplier(

                    rateLimiter,

                    Bulkhead.decorateSupplier(

                            bulkhead,

                            Retry.decorateSupplier(

                                    retry,

                                    () -> circuitBreakerFactory
                                            .create("userService")
                                            .run(

                                                    () ->
                                                            callUserServiceWithTimeout(
                                                                    order.userId()
                                                            ),

                                                    throwable -> {

                                                        throw new UserServiceException(

                                                                "User Service unavailable: "
                                                                        + throwable.getMessage(),

                                                                throwable
                                                        );
                                                    }
                                            )
                            )
                    )
            ).get();


        } catch (Exception ex) {

            System.out.println(

                    "Request failed, Rate Limit exceeded, "
                            + "or Bulkhead limit reached. "
                            + "Executing fallback."

            );


            user = fallbackUser(
                    order.userId()
            );
        }


        return new OrderResponse(

                order.orderId(),

                order.userId(),

                order.product(),

                order.quantity(),

                user
        );
    }


    // =================================
    // User Service Call with Timeout
    // =================================

    private UserResponse callUserServiceWithTimeout(
            Long userId
    ) {

        System.out.println(
                "Calling User Service for user: "
                        + userId
        );


        try {

            CompletableFuture<UserResponse> future =

                    CompletableFuture.supplyAsync(

                            () -> callUserService(userId),

                            executorService
                    );


            return TimeLimiter
                    .decorateFutureSupplier(

                            timeLimiter,

                            () -> future
                    )
                    .call();


        } catch (Exception ex) {

            System.out.println(

                    "User Service call timed out or failed: "
                            + ex.getMessage()
            );


            throw new UserServiceException(

                    "User Service timeout or communication failure",

                    ex
            );
        }
    }


    // =================================
    // Actual User Service REST Call
    // =================================

    private UserResponse callUserService(
            Long userId
    ) {

        return restClient.get()

                .uri(
                        "/api/users/{id}",
                        userId
                )

                .retrieve()

                .onStatus(

                        HttpStatusCode::isError,

                        (request, response) -> {

                            throw new UserServiceException(

                                    "User Service returned status: "
                                            + response.getStatusCode()
                            );
                        }
                )

                .body(UserResponse.class);
    }


    // =================================
    // Fallback Method
    // =================================

    private UserResponse fallbackUser(
            Long userId
    ) {

        System.out.println(

                "Fallback executed for User Service. "
                        + "User ID: "
                        + userId
        );


        return new UserResponse(

                userId,

                "User temporarily unavailable",

                "N/A"
        );
    }
}