package com.example.Day4payment_service.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.example.Day4payment_service.dto.PaymentResponse;

@Service
public class PaymentService {

    private final AtomicInteger attemptCount = new AtomicInteger(0);

    public PaymentResponse getPaymentById(Long id) {

        int attempt = attemptCount.incrementAndGet();

        System.out.println("Payment Service attempt: " + attempt);

        // Deliberately fail the first attempt
        if (attempt == 1) {
            throw new RuntimeException("Temporary Payment Service failure");
        }

        // Second attempt succeeds
        return new PaymentResponse(
                id,
                1L,
                "SUCCESS"
        );
    }
}