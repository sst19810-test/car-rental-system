package com.carrentalsystem.app.restcontroller;

import com.carrentalsystem.app.dto.BookingRequestDTO;
import com.carrentalsystem.app.dto.BookingResponseDTO;
import com.carrentalsystem.app.dto.CarDTO;
import com.carrentalsystem.app.dto.UserDTO;
import com.carrentalsystem.app.service.BookingService;
import com.carrentalsystem.app.service.CarService;
import com.carrentalsystem.app.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingRestController {

    private final BookingService bookingService;
    private final CarService carService;
    private final UserService userService;

    // ✅ USER: Book a car
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody @Valid BookingRequestDTO bookingRequestDTO, @AuthenticationPrincipal  UserDetails userDetails) {

            try {
                UserDTO user = userService.getUserByEmail(userDetails.getUsername());
                bookingRequestDTO.setUserId(user.getId());

                BookingResponseDTO savedBooking = bookingService.createBooking(bookingRequestDTO);
                return ResponseEntity.ok(savedBooking);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Failed to book: " + e.getMessage());
            }
    }
    // ✅ USER: View my bookings
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

    // ✅ USER: Cancel booking
    @PutMapping ("/cancel/{id}")
    public ResponseEntity<String> cancelBooking(@PathVariable("id") Integer bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok("Booking cancelled successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cancellation Failed : " + e.getMessage());
        }
    }



    // ✅ ADMIN: View booking details
    @GetMapping("/admin/view/{id}")
    public ResponseEntity<BookingResponseDTO> viewBookingDetails(@PathVariable("id") Integer id) {
        BookingResponseDTO booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }


    }



