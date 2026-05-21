package com.carrentalsystem.app.dto;

import com.carrentalsystem.app.helper.BookingStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingResponseDTO {
    private Integer id;
    private String userName;
    private String carName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String duration;
    private Double totalPrice;
    private BookingStatus status;
    private List<String> imageUrls;
}
