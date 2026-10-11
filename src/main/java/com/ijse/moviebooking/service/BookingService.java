package com.ijse.moviebooking.service;

import com.ijse.moviebooking.dto.request.BookingRequest;
import com.ijse.moviebooking.dto.response.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest bookingRequest, String username);
    BookingResponse getBookingById(Long id, String username);
    List<BookingResponse> getMyBookings(String username);
    List<BookingResponse> getAllBookings();
    BookingResponse cancelBooking(Long id, String username);
    void deleteBooking(Long id);
}
