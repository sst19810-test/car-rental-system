package com.carrentalsystem.app.dto;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Integer bookingId;
    private Double amount;
    private String paymentMethod;
}
