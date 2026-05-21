package com.carrentalsystem.app.restcontroller;

import com.carrentalsystem.app.dto.BookingResponseDTO;
import com.carrentalsystem.app.dto.PaymentRequestDTO;
import com.carrentalsystem.app.dto.UserDTO;
import com.carrentalsystem.app.helper.BookingStatus;
import com.carrentalsystem.app.helper.CarType;
import com.carrentalsystem.app.service.BookingService;
import com.carrentalsystem.app.service.CarService;
import com.carrentalsystem.app.service.PaymentService;
import com.carrentalsystem.app.service.UserService;
import com.carrentalsystem.app.util.FineCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserRestController {

    private final UserService userService;
    private final BookingService bookingService;
    private final CarService carService;
    private final PaymentService paymentService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String,Object>> showUserDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        UserDTO user = userService.getUserByEmail(email);

        List<BookingResponseDTO> upcomingBookings = bookingService.getUpcomingBookingsByUser(user.getId());

        Map<String , Object> response = new HashMap<>();
        response.put("Username",user);
        response.put("Upcoming Bookings",upcomingBookings);
        response.put("AvailableCars", carService.getAllAvailableCars());
        response.put("suvCars", carService.getCarByType(CarType.SUV));
        response.put("hatchbackCars", carService.getCarByType(CarType.HATCHBACK));
        response.put("sedanCars", carService.getCarByType(CarType.SEDAN));
        return ResponseEntity.ok(response);
    }
    @GetMapping("/myBookings")
    public ResponseEntity<?> getUserBookings(@AuthenticationPrincipal  UserDetails userDetails) {
        try {
            UserDTO user = userService.getUserByEmail(userDetails.getUsername());
            List<BookingResponseDTO> allBookings =bookingService.getBookingsByUserId(user.getId());
            return ResponseEntity.ok(allBookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("No Bookings Found: " + e.getMessage());
        }
    }





    @PutMapping("/return/{bookingId}")
    public ResponseEntity<String> returnCar(@PathVariable Integer bookingId) {
        bookingService.updateBookingStatus(bookingId, BookingStatus.COMPLETED.name());
        return ResponseEntity.accepted().body("Car Returned SuccessFully");
    }


}

