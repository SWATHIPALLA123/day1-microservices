package com.example.Day4payment_service.dto;

public class PaymentResponse {

    private Long id;
    private Long orderId;
    private String status;

    public PaymentResponse() {
    }

    public PaymentResponse(Long id, Long orderId, String status) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }
}