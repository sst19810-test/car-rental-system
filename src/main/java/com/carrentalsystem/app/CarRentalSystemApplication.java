package com.carrentalsystem.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CarRentalSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalSystemApplication.class, args);
        System.out.println("Car Rental System Application has started successfully!");

    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
