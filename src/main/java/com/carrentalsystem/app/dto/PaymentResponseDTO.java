package com.carrentalsystem.app.dto;

import com.carrentalsystem.app.helper.PaymentStatus;
import lombok.Data;

@Data
public class PaymentResponseDTO {
    private Integer id;
    private Integer bookingId;
    private Double amount;
    private String paymentMethod;
    private PaymentStatus status;
}
