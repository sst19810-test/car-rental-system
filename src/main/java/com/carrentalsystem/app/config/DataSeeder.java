package com.carrentalsystem.app.config;

import com.carrentalsystem.app.entity.*;
import com.carrentalsystem.app.helper.*;
import com.carrentalsystem.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class DataSeeder {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            if (userRepository.count() == 0) {
                // 🌱 Seed Users
                User admin = new User( "Admin", "admin@ridevia.com", passwordEncoder.encode("admin123"), "9990000001", Role.ADMIN);
                User user1 = new User( "Sourabh Singh", "user1@ridevia.com", passwordEncoder.encode("user123"), "8880000001", Role.USER);
                User user2 = new User( "Test User", "user2@ridevia.com", passwordEncoder.encode("user123"), "8880000002", Role.USER);
                userRepository.saveAll(List.of(admin, user1, user2));
            }

            if (carRepository.count() == 0) {
                // 🌱 Seed Cars
                Car car1 = new Car( "Toyota Camry", "Smooth and reliable", 2000.0, CarType.SEDAN, true, null);
                Car car2 = new Car("Honda City", "Fuel efficient", 1800.0, CarType.SEDAN, true, null);
                Car car3 = new Car( "Hyundai i20", "Perfect for city", 1500.0, CarType.HATCHBACK, true, null);
                Car car4 = new Car( "Mahindra XUV", "Powerful SUV", 2500.0, CarType.SUV, true, null);
                Car car5 = new Car( "Kia Seltos", "Stylish and comfy", 2300.0, CarType.SUV, true, null);
                carRepository.saveAll(List.of(car1, car2, car3, car4, car5));
            }



            System.out.println("🌱 Seeded users, cars, and bookings.");
        };
    }
}
