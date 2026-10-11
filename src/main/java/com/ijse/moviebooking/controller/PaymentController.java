package com.ijse.moviebooking.controller;

import com.ijse.moviebooking.dto.request.PaymentRequest;
import com.ijse.moviebooking.dto.response.PaymentResponse;
import com.ijse.moviebooking.enums.PaymentStatus;
import com.ijse.moviebooking.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest paymentRequest,
            Authentication authentication
    ) {
        PaymentResponse payment = paymentService.processPayment(paymentRequest, authentication.getName());
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest paymentRequest,
            Authentication authentication
    ) {
        PaymentResponse payment = paymentService.processPayment(paymentRequest, authentication.getName());
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentDetails(
            @PathVariable Long id,
            Authentication authentication
    ) {
        PaymentResponse payment = paymentService.getPaymentById(id, authentication.getName());
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> getPaymentByBookingId(
            @PathVariable Long bookingId,
            Authentication authentication
    ) {
        PaymentResponse payment = paymentService.getPaymentByBookingId(bookingId, authentication.getName());
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(@PathVariable Long id) {
        PaymentStatus status = paymentService.getPaymentStatus(id);
        return ResponseEntity.ok(Map.of("paymentId", id, "status", status));
    }
}
