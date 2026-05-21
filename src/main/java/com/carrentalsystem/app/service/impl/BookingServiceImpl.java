package com.carrentalsystem.app.service.impl;

import com.carrentalsystem.app.dto.BookingRequestDTO;
import com.carrentalsystem.app.dto.BookingResponseDTO;
import com.carrentalsystem.app.entity.Booking;
import com.carrentalsystem.app.entity.Car;
import com.carrentalsystem.app.entity.CarImage;
import com.carrentalsystem.app.entity.User;
import com.carrentalsystem.app.exception.ResourceNotFoundException;
import com.carrentalsystem.app.helper.BookingStatus;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import com.carrentalsystem.app.repository.BookingRepository;
import com.carrentalsystem.app.repository.CarRepository;
import com.carrentalsystem.app.repository.UserRepository;
import com.carrentalsystem.app.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static com.carrentalsystem.app.helper.EmailTemplate.buildBookingConfirmationEmail;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final EmailService emailService ;
    private final BookingRepository bookingRepository;
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final  long CACHE_TTL = 3600L;
    private final CarRepository carRepository;
    private final JdbcTemplate jdbcTemplate;

    private String bookingKey(String suffix){
        return "Bookings:"+suffix;
    }

    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + dto.getUserId()));

        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + dto.getCarId()));



        Booking booking = new Booking();
        booking.setUser(user);
        booking.setCar(car);
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setStatus(BookingStatus.PENDING);

        long hours = Duration.between(dto.getStartTime(), dto.getEndTime()).toHours();
        double total = hours * car.getPricePerHour();

        booking.setTotalPrice(total);

        car.setAvailable(false); // Block car until return
        Booking saved = bookingRepository.save(booking);
        String emailContent = buildBookingConfirmationEmail(user, booking);
        emailService.sendEmail(user.getEmail(), "RideVia Booking Confirmation", emailContent);

        redisService.delete(bookingKey("all"));
        redisService.delete(bookingKey("user:"+user.getId()));
        return mapToDTO(saved);
    }


    @Override
    public List<BookingResponseDTO> getBookingsByUserId(Integer userId) {
        System.out.println("Hello in service method");
        String key =  bookingKey("user:"+userId);
        List<BookingResponseDTO> bookingResponseDTOS ;
    
        bookingResponseDTOS = redisService.get(key);
        if(bookingResponseDTOS==null) {
            bookingResponseDTOS =
                    bookingRepository.findByUser_Id(userId)
                            .stream()
                            .map(this::mapToDTO)
                            .collect(Collectors.toList());
            redisService.set(key, bookingResponseDTOS,CACHE_TTL);
        }
        return bookingResponseDTOS;
    }

    public String getUserName(String username){
        return jdbcTemplate.execute ( "SELECT name FROM user Where username="+username);
    }

    @Override
    public List<BookingResponseDTO> getAllBookings() {
        String key = bookingKey("all");
        List<BookingResponseDTO> responseDtoLists;
        responseDtoLists = redisService.get(key);
        if(responseDtoLists==null){
           responseDtoLists =  bookingRepository.findAll()
                   .stream()
                   .map(this::mapToDTO)
                   .collect(Collectors.toList());
           redisService.set(key, responseDtoLists, CACHE_TTL );
        }
        return responseDtoLists;
    }

    @Override
    public BookingResponseDTO getBookingById(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        return mapToDTO(booking);
    }

    @Override
    public BookingResponseDTO updateBookingStatus(Integer bookingId, String newStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        try {
            BookingStatus status = BookingStatus.valueOf(newStatus.toUpperCase());
            booking.setStatus(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid booking status: " + newStatus);
        }
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            Car car = booking.getCar();
            if (car != null) {
                car.setAvailable(false); // Block car until return
                carRepository.save(car);
            }
        } else if (booking.getStatus() == BookingStatus.CANCELLED) {
            Car car = booking.getCar();
            if (car != null) {
                car.setAvailable(true); // Make car available again
                carRepository.save(car);
            }
        }
        Booking updated = bookingRepository.save(booking);
        return mapToDTO(updated);
    }

    @Override
    public void deleteBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        bookingRepository.delete(booking);
    }

    @Override
    public void cancelBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        Car car = booking.getCar();
        bookingRepository.save(booking);
        if(car != null) {
            car.setAvailable(true); // Make car available again
            carRepository.save(car);
        }

    }

    @Override
    public long getBookingCount() {
        return bookingRepository.count();
    }

    @Override
    public double calculateTotalProfit() {
        List<Booking> bk= bookingRepository.findAll();
        if(bk.isEmpty() || bk==null) {
            return 0;
        }
        return bk.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.COMPLETED)
                .mapToDouble(Booking::getTotalPrice)
                .sum();

    }

    @Override
    public List<BookingResponseDTO> getRecentBookings(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Booking> recentBookings = bookingRepository.findRecentBookings(pageable);

        return recentBookings.stream()
                .map(booking -> {
                    BookingResponseDTO dto = new BookingResponseDTO();
                    dto.setId(booking.getId());
                    dto.setUserName(booking.getUser().getName());
                    dto.setCarName(booking.getCar().getId()+" "+booking.getCar().getBrand() + " " + booking.getCar().getModel());
                    dto.setStartTime(booking.getStartTime());
                    dto.setEndTime(booking.getEndTime());
                    getBookingDTODuration(booking, dto);
                    dto.setTotalPrice(booking.getTotalPrice());
                    dto.setStatus(booking.getStatus());
                    return dto;
                })
                .toList();
    }

    @Override
    public List<BookingResponseDTO> getUpcomingBookingsByUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        List<Booking> bookings = bookingRepository.findByUserAndStatus(user, BookingStatus.CONFIRMED);

        return bookings.stream()
                .filter(booking -> booking.getStartTime().isAfter(java.time.LocalDateTime.now()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    private BookingResponseDTO mapToDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        dto.setCarName(booking.getCar().getBrand() + " " + booking.getCar().getModel());
        dto.setUserName(booking.getUser().getName());
        if (booking.getCar().getImages() != null) {
            List<String> urls = booking.getCar().getImages().stream()
                    .map(CarImage::getImageUrl)
                    .collect(Collectors.toList());
            dto.setImageUrls(urls);
        }
        getBookingDTODuration(booking, dto);

        return dto;
    }

    private void getBookingDTODuration(Booking booking, BookingResponseDTO dto) {
        long durationHours = Duration.between(booking.getStartTime(), booking.getEndTime()).toHours();
        if (durationHours < 24) {
            dto.setDuration(durationHours + " hours");
        } else {
            long days = durationHours / 24;
            long hours = durationHours % 24;
            dto.setDuration(days + " days " + hours + " hours");
        }
    }
}
