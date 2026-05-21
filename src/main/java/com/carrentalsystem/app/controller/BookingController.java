package com.carrentalsystem.app.controller;

import com.carrentalsystem.app.dto.BookingRequestDTO;
import com.carrentalsystem.app.dto.BookingResponseDTO;
import com.carrentalsystem.app.dto.CarDTO;
import com.carrentalsystem.app.dto.UserDTO;
import com.carrentalsystem.app.service.BookingService;
import com.carrentalsystem.app.service.CarService;
import com.carrentalsystem.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor

public class BookingController {

    private final BookingService bookingService;
    private final CarService carService;
    private final UserService userService;
    // ✅ ADMIN: View all bookings
    @GetMapping("admin/bookings")
    public String getAllBookings(Model model) {
        List<BookingResponseDTO> bookings = bookingService.getAllBookings();
        model.addAttribute("bookings", bookings);
        return "admin/allbookings";
    }
    // ✅ USER: Book a car
    @PostMapping("bookings/create")
    public String createBooking(@ModelAttribute @Valid BookingRequestDTO bookingRequestDTO,
                                RedirectAttributes redirectAttributes) {
        try {
            bookingService.createBooking(bookingRequestDTO);
            log.info("Booked Successfully");
           System.out.println("Success");
        } catch (Exception e) {
            log.error("Booked FAiled");

            System.out.println( "Failed to book: " + e.getMessage()  +"  Error becz -CarID "+ bookingRequestDTO.getCarId() +" \t UserId"+bookingRequestDTO.getUserId() +" at " + e.getStackTrace()[0]);
            e.printStackTrace();
        }

        return "redirect:/user/dashboard";
    }
    @GetMapping("/user/book")
    public String showBookingForm(@RequestParam(name = "id", required = false) Integer carId,
                                  Model model,@AuthenticationPrincipal UserDetails userDetails) {
        UserDTO user = userService.getUserByEmail(userDetails.getUsername());

        BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
        if(user!=null) {
            bookingRequestDTO.setUserId(user.getId());
        }// Get the userId from UserDTO
        if (carId != null) {
            bookingRequestDTO.setCarId(carId);
            model.addAttribute("car", carService.getCarById(carId)); // for car info display
        }



        model.addAttribute("bookingRequest", bookingRequestDTO);
        return "user/book"; // This will render user/book.html
    }

    // ✅ USER: View my bookings
    @GetMapping("user/bookings")
    public String getUserBookings(@AuthenticationPrincipal  UserDetails userDetails, Model model) {
        UserDTO user = userService.getUserByEmail(userDetails.getUsername());
        Integer userId = user.getId(); // Get the userId from the UserDTO
        String userName = user.getName(); // Get the username from UserDTO
        List<BookingResponseDTO> bookings = bookingService.getBookingsByUserId(userId);
        model.addAttribute("bookings", bookings);
        model.addAttribute("userName", userName);
        model.addAttribute("now", java.time.LocalDateTime.now()); // ✅ Add this// Add username to the model
        return "user/mybookings";
    }

    // ✅ USER: Cancel booking
    @GetMapping("bookings/cancel/{id}")
    public String cancelBooking(@PathVariable("id") Integer bookingId,
                                RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(bookingId);
            redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel booking: " + e.getMessage());
        }

        return "redirect:/user/bookings";
    }



    // ✅ ADMIN: View booking details
    @GetMapping("/admin/view/{id}")
    public String viewBookingDetails(@PathVariable("id") Integer id, Model model) {
        BookingResponseDTO booking = bookingService.getBookingById(id);
        model.addAttribute("booking", booking);
        return "admin/booking-details";
    }

    @GetMapping("/book/{carId}")
    public String showBookingForm(@PathVariable Integer carId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        UserDTO user = userService.getUserByEmail(userDetails.getUsername());

        CarDTO car = carService.getCarById(carId);
        BookingRequestDTO bookingRequest = new BookingRequestDTO();
        bookingRequest.setCarId(carId); // set carId directly
        bookingRequest.setUserId(user.getId()); // set userId from session

        model.addAttribute("car", car);
        model.addAttribute("bookingRequest", bookingRequest);

        return "user/book";
    }

    @PostMapping("/book")
    public String bookCar(@ModelAttribute("bookingRequest") BookingRequestDTO dto,
                          @AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
        try {
            UserDTO user = userService.getUserByEmail(userDetails.getUsername());
            dto.setUserId(user.getId());

            BookingResponseDTO savedBooking = bookingService.createBooking(dto); // must return saved booking
            Integer bookingId = savedBooking.getId();

            redirectAttributes.addFlashAttribute("success", "Booking initiated. Please complete payment.");
            return "redirect:/payment/" + bookingId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Booking failed: " + e.getMessage());
            return "redirect:/user/dashboard";
        }
    }


}
