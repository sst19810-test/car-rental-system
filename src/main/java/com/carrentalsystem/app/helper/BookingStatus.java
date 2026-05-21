package com.carrentalsystem.app.helper;

import lombok.Getter;

@Getter
public enum BookingStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed");

    private final String status;

    BookingStatus(String status) {
        this.status = status;
    }

}
