package com.carrentalsystem.app.helper;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    SUCCESS("Success"),
    FAILURE("Failure"),
    PENDING("Pending"),
    CANCELLED("Cancelled");

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }

}
