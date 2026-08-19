package com.example.orderservice.dto;

public record OrderResponse(
        Long orderId,
        Long userId,
        String product,
        Integer quantity,
        UserResponse user
) {
}
