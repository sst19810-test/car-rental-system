package com.carrentalsystem.app.service.impl;

import com.carrentalsystem.app.dto.CarDTO;
import com.carrentalsystem.app.dto.CarUploadDTO;
import com.carrentalsystem.app.entity.Car;
import com.carrentalsystem.app.entity.CarImage;
import com.carrentalsystem.app.exception.ResourceNotFoundException;
import com.carrentalsystem.app.helper.CarType;
import com.carrentalsystem.app.repository.CarImageRepository;
import com.carrentalsystem.app.repository.CarRepository;
import com.carrentalsystem.app.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarImageRepository carImageRepository;
    private final RedisService redisService;
    private final  long CACHE_TTL = 3600L;

    private String key(String suffix){
        return "car:"+suffix;
    }

    @Value("${car.upload.dir}")
    private String uploadDir;

    @Override
    public List<CarDTO> getAllAvailableCars() {
        String key = key("avl");
        List<CarDTO> carDTOS;
        carDTOS= redisService.get(key);
        if(carDTOS==null){

        carDTOS= carRepository.findAll()
                .stream()
                .filter(Car::isAvailable)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        redisService.set(key,carDTOS,CACHE_TTL);
        }
        return carDTOS;
    }

    @Override
    public List<CarDTO> getAllCars() {
        String key = key("all");
        List<CarDTO> carDTOList;
        carDTOList=redisService.get(key);
        if(carDTOList==null) {
          carDTOList =  carRepository.findAll()
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
          redisService.set(key,carDTOList,CACHE_TTL);

        }
        return carDTOList;
    }

    @Override
    public CarDTO getCarById(Integer id) {
        String key = key("id:"+id);
        CarDTO car ;
        try{car = redisService.get(key );} catch (Exception e){
            car = null ;
        }
        if(car==null){
            Car car1=carRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + id));
            car =  mapToDTO(car1);
            redisService.set(key,car,CACHE_TTL);
        }
        return car;
    }

    @Override
    public CarDTO addCar(CarUploadDTO carUploadDTO) {
        Car car = new Car();
        mapUploadDTOToEntity(carUploadDTO, car);
        car.setAvailable(true);
        Car savedCar = carRepository.save(car);
        saveImages(carUploadDTO.getImages(), savedCar);
        redisService.delete(key("id:"+savedCar.getId()));
        redisService.delete(key("all"));
        return mapToDTO(savedCar);
    }

    @Override
    public CarDTO updateCar(Integer id, CarUploadDTO carUploadDTO) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found for update with ID: " + id));
        mapUploadDTOToEntity(carUploadDTO, car);
        Car updated = carRepository.save(car);
        redisService.delete(key("id:"+updated.getId()));
        redisService.delete(key("all"));
        return mapToDTO(updated);
    }

    @Override
    public void deleteCar(Integer id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found for delete with ID: " + id));
        carRepository.delete(car);
    }

    @Override
    public List<CarDTO> getCarByType(CarType type) {

       return carRepository.findByType(type).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addCarWithImages(CarUploadDTO carDTO, List<MultipartFile> imageFiles) {
        Car car = new Car();
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setColor(carDTO.getColor());
        car.setType(carDTO.getType());
        car.setFuelType(carDTO.getFuelType());
        car.setDescription(carDTO.getDescription());
        car.setPricePerHour(carDTO.getPricePerHour());
        car.setAvailable(true); // default availability

        Car savedCar = carRepository.save(car);

        List<CarImage> imageEntities = new ArrayList<>();

        for (MultipartFile file : imageFiles) {
            if (!file.isEmpty()) {
                try {
                    // Create unique file name
                    String fileName = "car_" + savedCar.getId() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    String uploadDir = "uploads/";

                    // Save image to local directory
                    Path filePath = Paths.get(uploadDir, fileName);
                    Files.createDirectories(filePath.getParent());
                    Files.write(filePath, file.getBytes());

                    // Save image metadata in DB
                    CarImage image = new CarImage();
                    image.setCar(savedCar);
                    image.setImageUrl("/uploads/" + fileName); // Thymeleaf static mapping
                    imageEntities.add(image);

                } catch (IOException e) {
                    throw new RuntimeException("Failed to store image: " + file.getOriginalFilename(), e);
                }
            }
        }

        if (!imageEntities.isEmpty()) {
            carImageRepository.saveAll(imageEntities);
        }
    }

    @Override
    public void addCarWithImages(Integer id,  List<MultipartFile> imageFiles) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found for adding images with ID: " + id));

        saveImages(imageFiles, car);

    }

    @Override
    public long countAvailableCars() {
        String key = key("count");
        Integer count = redisService.get(key);
        if(count==null){
            count = carRepository.findAllByAvailable(true).size();
            redisService.set(key,count,CACHE_TTL);
        }
       return count;
    }

    @Override
    public void deleteImage(CarImage image) {
        carImageRepository.delete(image);

    }

    @Override
    public CarImage findByImageUrl(String imageUrl) {
        CarImage carImage =  carImageRepository.findByImageUrl(imageUrl).get(0);
        if(carImage == null) {
            throw new ResourceNotFoundException("Image not found with URL: " + imageUrl);
        }
        return carImage;
    }


    // Helper Methods
    private void mapUploadDTOToEntity(CarUploadDTO dto, Car car) {
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setColor(dto.getColor());
        car.setType(dto.getType());
        car.setFuelType(dto.getFuelType());
        car.setDescription(dto.getDescription());
        car.setPricePerHour(dto.getPricePerHour());
    }

    private void saveImages(List<MultipartFile> files, Car car) {
        List<CarImage> imageList = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                try {
                    String fileName = "car_" + car.getId() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir, fileName);
                    Files.createDirectories(filePath.getParent());
                    Files.write(filePath, file.getBytes());

                    CarImage image = new CarImage();
                    image.setImageUrl("/uploads/" + fileName);  // URL for Thymeleaf/static access
                    image.setCar(car);
                    imageList.add(image);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload car image: " + file.getOriginalFilename(), e);
                }
            }
        }

        if (!imageList.isEmpty()) {
            carImageRepository.saveAll(imageList);
        }
    }


    private CarDTO mapToDTO(Car car) {
        CarDTO dto = new CarDTO();
        dto.setId(car.getId());
        dto.setBrand(car.getBrand());
        dto.setModel(car.getModel());
        dto.setColor(car.getColor());
        dto.setType(car.getType());
        dto.setFuelType(car.getFuelType());
        dto.setAvailable(car.isAvailable());
        dto.setDescription(car.getDescription());
        dto.setPricePerHour(car.getPricePerHour());

        if (car.getImages() != null) {
            List<String> urls = car.getImages().stream()
                    .map(CarImage::getImageUrl)
                    .collect(Collectors.toList());
            dto.setImageUrls(urls);
        }
        return dto;
    }



}
