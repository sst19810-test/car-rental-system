package com.carrentalsystem.app.service;

import com.carrentalsystem.app.dto.BookingRequestDTO;
import com.carrentalsystem.app.dto.BookingResponseDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO dto);
    List<BookingResponseDTO> getBookingsByUserId(Integer userId);
    List<BookingResponseDTO> getAllBookings();
    BookingResponseDTO getBookingById(Integer bookingId);
    BookingResponseDTO updateBookingStatus(Integer bookingId, String newStatus); // Optional
    void deleteBooking(Integer bookingId);
    void cancelBooking(Integer bookingId);
    long getBookingCount();

    double calculateTotalProfit();

    List<BookingResponseDTO> getRecentBookings(int i);

    List<BookingResponseDTO> getUpcomingBookingsByUser(Integer id);
}
