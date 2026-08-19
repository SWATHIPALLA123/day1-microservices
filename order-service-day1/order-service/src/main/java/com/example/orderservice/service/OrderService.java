package com.example.orderservice.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.example.orderservice.dto.OrderData;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.UserResponse;

@Service
public class OrderService {

    private final RestClient restClient;
    private final Map<Long, OrderData> orders = Map.of(
        101L, new OrderData(101L, 1L, "Laptop", 1),
        102L, new OrderData(102L, 2L, "Keyboard", 2),
        103L, new OrderData(103L, 3L, "Mouse", 1)
    );

    public OrderService(@Value("${user-service.base-url}") String userServiceBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(userServiceBaseUrl)
                .build();
    }

    public OrderResponse getOrderById(Long orderId) {
        OrderData order = orders.get(orderId);

        if (order == null) {
            throw new OrderNotFoundException("Order not found with id: " + orderId);
        }

        UserResponse user;
        try {
            user = restClient.get()
                    .uri("/api/users/{id}", order.userId())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new UserServiceException(
                            "User Service returned status: " + response.getStatusCode()
                        );
                    })
                    .body(UserResponse.class);
        } catch (RestClientException ex) {
            throw new UserServiceException(
                "Unable to communicate with User Service: " + ex.getMessage(), ex
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
}
