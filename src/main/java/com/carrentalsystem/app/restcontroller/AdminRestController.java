package com.carrentalsystem.app.restcontroller;

import com.carrentalsystem.app.dto.BookingResponseDTO;
import com.carrentalsystem.app.dto.UserDTO;
import com.carrentalsystem.app.entity.User;
import com.carrentalsystem.app.exception.ResourceNotFoundException;
import com.carrentalsystem.app.service.BookingService;
import com.carrentalsystem.app.service.CarService;
import com.carrentalsystem.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminRestController {

    private final UserService userService;
    private final BookingService bookingService;
    private final CarService carService;


  @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {
        Map<String, Object> response = new HashMap<>();
        response.put("bookingsCount", bookingService.getBookingCount());
        response.put("totalProfit", bookingService.calculateTotalProfit());
        response.put("totalCarsCount", carService.getAllCars().size());
        response.put("availableCarsCount", carService.countAvailableCars());
        response.put("recentBookings", bookingService.getRecentBookings(6));
        return ResponseEntity.ok(response);
    }



    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> viewUsers() {
        List<UserDTO> users = userService.getAllUsers();
        if(users==null) throw new ResourceNotFoundException("No User Found");
        return ResponseEntity.ok(users);
    }

    @GetMapping("users/{id}")
    public  ResponseEntity<Map<UserDTO,List<BookingResponseDTO>>> viewUserDetails(@PathVariable Integer id, Model model) {
        UserDTO user = userService.getUserById(id);
        List<BookingResponseDTO> bookings = bookingService.getBookingsByUserId(id);
        if(user==null) throw new ResourceNotFoundException("No User Found");
        Map<UserDTO,List<BookingResponseDTO>> map = new HashMap<>();
        map.put(user,bookings);
        return ResponseEntity.ok(map);
    }

    @GetMapping("/allbookings")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings(Model model) {
        List<BookingResponseDTO> bookings = bookingService.getAllBookings();
        if(bookings==null) throw new ResourceNotFoundException("No Booking Found");
        return ResponseEntity.ok(bookings);
    }

}
