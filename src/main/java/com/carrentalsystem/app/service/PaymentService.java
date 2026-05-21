package com.carrentalsystem.app.service;

import com.carrentalsystem.app.dto.PaymentRequestDTO;
import com.carrentalsystem.app.dto.PaymentResponseDTO;

import java.util.List;

public interface PaymentService {
    PaymentResponseDTO makePayment(PaymentRequestDTO paymentRequestDTO);
    PaymentResponseDTO getPaymentByBookingId(Integer bookingId);
    List<PaymentResponseDTO> getAllPayments();
    void deletePayment(Integer paymentId);
}
