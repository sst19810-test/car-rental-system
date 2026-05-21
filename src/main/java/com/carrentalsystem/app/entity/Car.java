package com.carrentalsystem.app.entity;

import com.carrentalsystem.app.helper.CarType;
import com.carrentalsystem.app.helper.FuelType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Color is required")
    private String color;

    @Enumerated(EnumType.STRING)
    @NotBlank(message = "Type is required")
    private CarType type;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Fuel type is required")
    private FuelType fuelType;

    private boolean available;

    @Size(max = 500)
    private String description;

    @Positive(message = "Price per hour must be positive")
    private Double pricePerHour;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CarImage> images;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    public Car(String toyotaCamry, String smoothAndReliable, double v, CarType carType, boolean b, Object o) {
        this.brand = toyotaCamry;
        this.model = "Camry";
        this.color = "Blue";
        this.type = carType;
        this.fuelType = FuelType.PETROL;
        this.available = b;
        this.description = smoothAndReliable;
        this.pricePerHour = v;
    }
}
