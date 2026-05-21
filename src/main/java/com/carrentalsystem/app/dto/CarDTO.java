package com.carrentalsystem.app.dto;

import com.carrentalsystem.app.helper.CarType;
import com.carrentalsystem.app.helper.FuelType;
import lombok.Data;

import java.util.List;

@Data
public class CarDTO {
    private Integer id;
    private String brand;
    private String model;
    private String color;
    private CarType type;
    private FuelType fuelType;
    private boolean available;
    private String description;
    private Double pricePerHour;
    private List<String> imageUrls;
}
