package com.carrentalsystem.app.controller;

import com.carrentalsystem.app.dto.BookingResponseDTO;
import com.carrentalsystem.app.dto.CarDTO;
import com.carrentalsystem.app.dto.UserDTO;
import com.carrentalsystem.app.service.BookingService;
import com.carrentalsystem.app.service.CarService;
import com.carrentalsystem.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final BookingService bookingService;
    private final CarService carService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        long bookingsCount = bookingService.getBookingCount();
        double totalProfit = bookingService.calculateTotalProfit();
        long availableCarsCount = carService.countAvailableCars();
        List<BookingResponseDTO> recentBookings = bookingService.getRecentBookings(6);

        model.addAttribute("bookingsCount", bookingsCount);
        model.addAttribute("totalProfit", totalProfit);
        model.addAttribute("totalCarsCount", carService.getAllCars().size());
        model.addAttribute("availableCarsCount", availableCarsCount);
        model.addAttribute("recentBookings", recentBookings);

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String viewUsers(Model model) {
        List<UserDTO> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }
 @GetMapping("users/{id}")
    public String viewUserDetails(@PathVariable Integer id, Model model) {
        UserDTO user = userService.getUserById(id);
        List<BookingResponseDTO> bookings = bookingService.getBookingsByUserId(id);
        model.addAttribute("user", user);
        model.addAttribute("bookings", bookings);
        return "admin/userDetails";
    }

    @GetMapping("/allbookings")
    public String getAllBookings(Model model) {
        List<BookingResponseDTO> bookings = bookingService.getAllBookings();
        // Example to fetch a car, can be removed if not needed
//
        model.addAttribute("bookings", bookings);


        return "admin/allbookings";
    }
}
