package com.carrentalsystem.app.repository;

import com.carrentalsystem.app.entity.CarImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarImageRepository extends JpaRepository<CarImage,Integer> {

   List<CarImage> findByImageUrl(String imageUrl);

}
