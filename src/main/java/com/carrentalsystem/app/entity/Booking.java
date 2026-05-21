package com.carrentalsystem.app.entity;

import com.carrentalsystem.app.helper.BookingStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    @NotNull(message = "Car must be selected")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User must be selected")
    private User user;

    @Column(columnDefinition = "DATETIME(0)")
    @FutureOrPresent(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @Column(columnDefinition = "DATETIME(0)")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;

    @Positive(message = "Total price must be positive")
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @NotNull
    private BookingStatus status;

    @PrePersist
    @PreUpdate
    private void truncateToMinute() {
        if (startTime != null) {
            startTime = startTime.withSecond(0).withNano(0);
        }
        if (endTime != null) {
            endTime = endTime.withSecond(0).withNano(0);
        }
    }
}