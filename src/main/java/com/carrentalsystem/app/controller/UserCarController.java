package com.carrentalsystem.app.controller;

import com.carrentalsystem.app.dto.CarDTO;
import com.carrentalsystem.app.dto.UserDTO;
import com.carrentalsystem.app.helper.CarType;
import com.carrentalsystem.app.service.CarService;
import com.carrentalsystem.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/cars")
public class UserCarController {

    private final CarService carService;
private  final UserService userService;


    @GetMapping
public String listAvailableCars(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String sort,
        @RequestParam(required = false, defaultValue = "false") boolean availableOnly,
        @AuthenticationPrincipal UserDetails userDetails,
        Model model) {

    List<CarDTO> cars = carService.getAllCars();

    String email = userDetails.getUsername();
    UserDTO user = userService.getUserByEmail(email);

    model.addAttribute("userName", user.getName());
    if (keyword != null && !keyword.trim().isEmpty()) {
        String lowerKeyword = keyword.toLowerCase();
        cars = cars.stream()
                .filter(car -> car.getBrand().toLowerCase().contains(lowerKeyword)
                        || car.getModel().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    if (type != null && !type.trim().isEmpty() && CarType.isValidType(type)) {
        cars = cars.stream().filter(car -> car.getType().getType().equalsIgnoreCase(type)).collect(Collectors.toList());
    }

    if (availableOnly) {
        cars = cars.stream().filter(CarDTO::isAvailable).collect(Collectors.toList());
    }

    if ("asc".equalsIgnoreCase(sort)) {
        cars.sort(Comparator.comparingDouble(CarDTO::getPricePerHour));
    } else if ("desc".equalsIgnoreCase(sort)) {
        cars.sort(Comparator.comparingDouble(CarDTO::getPricePerHour).reversed());
    }

    model.addAttribute("cars", cars);
    model.addAttribute("keyword", keyword);
    model.addAttribute("type", type);
    model.addAttribute("sort", sort);
    model.addAttribute("carTypes", CarType.values());
    model.addAttribute("availableOnly", availableOnly);

    return "user/cars";
}

    // 🗂 All Cars List (for admin or full view)
    @GetMapping("/all")
    public String listAllCars(Model model) {
        List<CarDTO> allCars = carService.getAllCars();
        model.addAttribute("cars", allCars);
        return "user/cars";
    }

    // 🔍 View Car Details
    @GetMapping("/view/{id}")
    public String viewCarDetails(@PathVariable Integer id, Model model) {
        CarDTO car = carService.getCarById(id);
        model.addAttribute("car", car);
        return "user/view";
    }
}
