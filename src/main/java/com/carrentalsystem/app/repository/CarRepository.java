package com.carrentalsystem.app.repository;

import com.carrentalsystem.app.dto.CarDTO;
import com.carrentalsystem.app.entity.Car;
import com.carrentalsystem.app.helper.CarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {
    List<Car> findAllByAvailable(boolean available);
    // Additional query methods can be defined here if needed


    @Override
    void deleteById(Integer integer);

    List<Car> findByType(CarType type);
}
