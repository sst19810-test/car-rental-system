package com.carrentalsystem.app.dto;

import com.carrentalsystem.app.helper.CarType;
import com.carrentalsystem.app.helper.FuelType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CarUploadDTO {
    private String brand;
    private String model;
    private String color;
    private CarType type;
    private FuelType fuelType;
    private String description;
    private Double pricePerHour;
    private List<MultipartFile> images;
}
