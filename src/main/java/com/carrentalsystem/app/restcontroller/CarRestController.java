package com.carrentalsystem.app.restcontroller;

import com.carrentalsystem.app.dto.CarDTO;
import com.carrentalsystem.app.dto.CarUploadDTO;
import com.carrentalsystem.app.entity.CarImage;
import com.carrentalsystem.app.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/car")
public class CarRestController {

    private final CarService carService;

    // ✅ Get all cars
    @GetMapping
    public ResponseEntity<List<CarDTO>> listCars() {
        List<CarDTO> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    // ✅ Get a single car by id
    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCar(@PathVariable Integer id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    // ✅ Add new car with images
    @PostMapping
    public ResponseEntity<String> addCar(
            @Valid @RequestPart("car") CarUploadDTO carDTO,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {

        carService.addCarWithImages(carDTO, imageFiles);
        return ResponseEntity.ok("Car added successfully!");
    }

    // ✅ Update car with optional new images
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCar(
            @PathVariable("id") Integer id,
            @Valid @RequestPart("car") CarUploadDTO carDTO,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {

        carService.updateCar(id, carDTO);
        if (imageFiles != null && !imageFiles.isEmpty()) {
            carService.addCarWithImages(id, imageFiles);
        }
        return ResponseEntity.ok("Car updated successfully!");
    }

    // ✅ Delete car
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCar(@PathVariable("id") Integer id) {
        carService.deleteCar(id);
        return ResponseEntity.ok("Car deleted successfully!");
    }

    // ✅ Delete car image
    @DeleteMapping("/{id}/delete-image")
    public ResponseEntity<String> deleteCarImage(
            @PathVariable("id") Integer carId,
            @RequestParam("url") String imageUrl) throws Exception {

        CarImage image = carService.findByImageUrl(imageUrl);
        String fileName = imageUrl.replace("/uploads/", "");
        Path filePath = Paths.get("uploads").resolve(fileName);

        Files.deleteIfExists(filePath);
        carService.deleteImage(image);

        return ResponseEntity.ok("Image deleted successfully!");
    }
}
