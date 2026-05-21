package com.carrentalsystem.app.repository;

import com.carrentalsystem.app.entity.Booking;
import com.carrentalsystem.app.entity.User;
import com.carrentalsystem.app.helper.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Integer> {
    List<Booking> findByUser_Id(Integer userId);

    List<Booking> findAllByUser_Id(Integer userId);
    @Query("SELECT b FROM Booking b ORDER BY b.startTime DESC")
    List<Booking> findRecentBookings(Pageable pageable);

    Integer user(User user);

    List<Booking> findByUserAndStatus(User user, BookingStatus bookingStatus);
}
