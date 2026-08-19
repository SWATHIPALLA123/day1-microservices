package com.example.orderservice.dto;

public record OrderData(Long orderId, Long userId, String product, Integer quantity) {
}
