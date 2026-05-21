package com.carrentalsystem.app.entity;

import com.carrentalsystem.app.helper.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    @NotNull
    private Booking booking;

    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentStatus status;
}
