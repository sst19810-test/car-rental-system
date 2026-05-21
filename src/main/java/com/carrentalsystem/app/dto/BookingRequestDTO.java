package com.carrentalsystem.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequestDTO {
    private Integer userId;
    private Integer carId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
