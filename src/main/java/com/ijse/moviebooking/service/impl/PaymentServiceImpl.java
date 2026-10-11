package com.ijse.moviebooking.service.impl;

import com.ijse.moviebooking.dto.request.PaymentRequest;
import com.ijse.moviebooking.dto.response.PaymentResponse;
import com.ijse.moviebooking.entity.Booking;
import com.ijse.moviebooking.entity.Payment;
import com.ijse.moviebooking.entity.User;
import com.ijse.moviebooking.enums.BookingStatus;
import com.ijse.moviebooking.enums.PaymentStatus;
import com.ijse.moviebooking.enums.Role;
import com.ijse.moviebooking.exception.BadRequestException;
import com.ijse.moviebooking.exception.ResourceNotFoundException;
import com.ijse.moviebooking.exception.UnauthorizedException;
import com.ijse.moviebooking.repository.BookingRepository;
import com.ijse.moviebooking.repository.PaymentRepository;
import com.ijse.moviebooking.repository.UserRepository;
import com.ijse.moviebooking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest paymentRequest, String username) {
        log.info("Processing payment for booking ID: {} by user: {}", paymentRequest.getBookingId(), username);

        Booking booking = bookingRepository.findById(paymentRequest.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + paymentRequest.getBookingId()));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"));

        if (!currentUser.getRole().equals(Role.ADMIN) && !booking.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to make a payment for this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Cancelled bookings cannot be paid for");
        }

        if (booking.getPayment() != null && booking.getPayment().getStatus() == PaymentStatus.COMPLETED) {
            throw new BadRequestException("Payment for this booking has already been completed");
        }

        Payment payment = booking.getPayment();
        if (payment == null) {
            payment = Payment.builder()
                    .booking(booking)
                    .amount(booking.getTotalAmount())
                    .paymentDate(LocalDateTime.now())
                    .paymentMethod(paymentRequest.getPaymentMethod())
                    .status(PaymentStatus.COMPLETED)
                    .build();
        } else {
            payment.setPaymentMethod(paymentRequest.getPaymentMethod());
            payment.setPaymentDate(LocalDateTime.now());
            payment.setStatus(PaymentStatus.COMPLETED);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment processed successfully with ID: {}, Booking ID {} set to CONFIRMED", savedPayment.getId(), booking.getId());

        return mapToPaymentResponse(savedPayment);
    }

    @Override
    public PaymentResponse getPaymentById(Long id, String username) {
        log.info("Fetching payment by ID: {} for user: {}", id, username);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"));

        if (!currentUser.getRole().equals(Role.ADMIN) && !payment.getBooking().getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to view this payment");
        }

        return mapToPaymentResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByBookingId(Long bookingId, String username) {
        log.info("Fetching payment by booking ID: {} for user: {}", bookingId, username);
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking id: " + bookingId));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"));

        if (!currentUser.getRole().equals(Role.ADMIN) && !payment.getBooking().getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to view this payment");
        }

        return mapToPaymentResponse(payment);
    }

    @Override
    public PaymentStatus getPaymentStatus(Long id) {
        log.info("Checking payment status for ID: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return payment.getStatus();
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        Booking booking = payment.getBooking();
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(booking.getId())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .movieTitle(booking.getShow().getMovie().getTitle())
                .theatreName(booking.getShow().getTheatre().getName())
                .seatNumbers(booking.getSeatNumbers())
                .build();
    }
}
