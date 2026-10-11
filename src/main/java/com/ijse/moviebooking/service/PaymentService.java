package com.ijse.moviebooking.service;

import com.ijse.moviebooking.dto.request.PaymentRequest;
import com.ijse.moviebooking.dto.response.PaymentResponse;
import com.ijse.moviebooking.enums.PaymentStatus;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest paymentRequest, String username);
    PaymentResponse getPaymentById(Long id, String username);
    PaymentResponse getPaymentByBookingId(Long bookingId, String username);
    PaymentStatus getPaymentStatus(Long id);
}
