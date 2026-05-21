package com.carrentalsystem.app.service;

import com.carrentalsystem.app.dto.CarDTO;
import com.carrentalsystem.app.dto.CarUploadDTO;
import com.carrentalsystem.app.entity.CarImage;
import com.carrentalsystem.app.helper.CarType;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CarService {
    List<CarDTO> getAllAvailableCars();
    List<CarDTO> getAllCars();
    CarDTO getCarById(Integer id);
    CarDTO addCar(CarUploadDTO carUploadDTO);
    CarDTO updateCar(Integer id, CarUploadDTO carUploadDTO);
    void deleteCar(Integer id);
    List<CarDTO> getCarByType(CarType type);
    void addCarWithImages(@Valid CarUploadDTO carDTO, List<MultipartFile> imageFiles);
    void addCarWithImages(Integer id, List<MultipartFile> imageFiles);
    long countAvailableCars();

    void deleteImage(CarImage image);

    CarImage findByImageUrl(String imageUrl);
}
