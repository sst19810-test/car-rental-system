package com.carrentalsystem.app.service.impl;

import com.carrentalsystem.app.dto.PaymentRequestDTO;
import com.carrentalsystem.app.dto.PaymentResponseDTO;
import com.carrentalsystem.app.entity.Booking;
import com.carrentalsystem.app.entity.Payment;
import com.carrentalsystem.app.exception.ResourceNotFoundException;
import com.carrentalsystem.app.helper.PaymentStatus;
import com.carrentalsystem.app.repository.BookingRepository;
import com.carrentalsystem.app.repository.PaymentRepository;
import com.carrentalsystem.app.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final BookingRepository bookingRepository;

    @Override
    public PaymentResponseDTO makePayment(PaymentRequestDTO dto) {
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + dto.getBookingId()));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(PaymentStatus.SUCCESS); // Simulated success — can later randomize or validate

        Payment saved = paymentRepository.save(payment);
        return mapToDTO(saved);
    }

    @Override
    public PaymentResponseDTO getPaymentByBookingId(Integer bookingId) {
        return paymentRepository.findAll()
                .stream()
                .filter(p -> p.getBooking().getId().equals(bookingId))
                .findFirst()
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No payment found for booking ID: " + bookingId));
    }

    @Override
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePayment(Integer paymentId) {
        if (!paymentRepository.existsById(paymentId)) {
            throw new ResourceNotFoundException("Payment not found with ID: " + paymentId);
        }
        paymentRepository.deleteById(paymentId);
    }

    private PaymentResponseDTO mapToDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setBookingId(payment.getBooking().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        return dto;
    }
}
