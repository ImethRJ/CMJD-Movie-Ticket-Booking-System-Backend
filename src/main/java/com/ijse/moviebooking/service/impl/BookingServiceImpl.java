package com.ijse.moviebooking.service.impl;

import com.ijse.moviebooking.dto.request.BookingRequest;
import com.ijse.moviebooking.dto.response.*;
import com.ijse.moviebooking.entity.*;
import com.ijse.moviebooking.enums.*;
import com.ijse.moviebooking.exception.BadRequestException;
import com.ijse.moviebooking.exception.ResourceNotFoundException;
import com.ijse.moviebooking.exception.SeatAlreadyBookedException;
import com.ijse.moviebooking.exception.UnauthorizedException;
import com.ijse.moviebooking.repository.BookingRepository;
import com.ijse.moviebooking.repository.ShowRepository;
import com.ijse.moviebooking.repository.UserRepository;
import com.ijse.moviebooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest, String username) {
        log.info("Creating booking for user: {}, show ID: {}", username, bookingRequest.getShowId());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Show show = showRepository.findById(bookingRequest.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + bookingRequest.getShowId()));

        if (show.getStatus() == ShowStatus.CANCELLED) {
            throw new BadRequestException("A cancelled show cannot accept bookings");
        }
        if (show.getStatus() == ShowStatus.COMPLETED) {
            throw new BadRequestException("A completed show cannot accept new bookings");
        }

        List<String> requestedSeats = bookingRequest.getSeatNumbers();

        if (requestedSeats == null || requestedSeats.isEmpty()) {
            throw new BadRequestException("A booking must contain at least one seat");
        }

        if (!bookingRequest.getNumberOfTickets().equals(requestedSeats.size())) {
            throw new BadRequestException("The number of tickets (" + bookingRequest.getNumberOfTickets()
                    + ") must match the number of selected seat numbers (" + requestedSeats.size() + ")");
        }

        Set<String> uniqueSeats = new HashSet<>(requestedSeats);
        if (uniqueSeats.size() < requestedSeats.size()) {
            throw new BadRequestException("Duplicate seats found in booking request");
        }

        List<String> alreadyBookedSeats = bookingRepository.findBookedSeatsByShowIdAndStatuses(
                show.getId(), List.of(BookingStatus.CONFIRMED, BookingStatus.PENDING));

        for (String seat : requestedSeats) {
            if (alreadyBookedSeats.contains(seat)) {
                throw new SeatAlreadyBookedException("Seat " + seat + " is already booked for this show");
            }
        }

        BigDecimal totalAmount = show.getTicketPrice().multiply(BigDecimal.valueOf(bookingRequest.getNumberOfTickets()));

        Booking booking = Booking.builder()
                .user(user)
                .show(show)
                .seatNumbers(requestedSeats)
                .numberOfTickets(bookingRequest.getNumberOfTickets())
                .totalAmount(totalAmount)
                .bookingDate(LocalDateTime.now())
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with ID: {}", savedBooking.getId());

        return mapToBookingResponse(savedBooking);
    }

    @Override
    public BookingResponse getBookingById(Long id, String username) {
        log.info("Fetching booking by ID: {} for user: {}", id, username);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"));

        if (!currentUser.getRole().equals(Role.ADMIN) && !booking.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to view this booking");
        }

        return mapToBookingResponse(booking);
    }

    @Override
    public List<BookingResponse> getMyBookings(String username) {
        log.info("Fetching bookings for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return bookingRepository.findByUserId(user.getId()).stream()
                .map(this::mapToBookingResponse)
                .toList();
    }

    @Override
    public List<BookingResponse> getAllBookings() {
        log.info("Fetching all bookings (ADMIN)");
        return bookingRepository.findAll().stream()
                .map(this::mapToBookingResponse)
                .toList();
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long id, String username) {
        log.info("Cancelling booking ID: {} by user: {}", id, username);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"));

        if (!currentUser.getRole().equals(Role.ADMIN) && !booking.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        Payment payment = booking.getPayment();
        if (payment != null && payment.getStatus() == PaymentStatus.COMPLETED) {
            payment.setStatus(PaymentStatus.REFUNDED);
        }

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking ID {} successfully cancelled", id);

        return mapToBookingResponse(updatedBooking);
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        log.info("Deleting booking ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        bookingRepository.delete(booking);
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        User u = booking.getUser();
        Show s = booking.getShow();
        Movie m = s.getMovie();
        Theatre t = s.getTheatre();

        UserResponse userResponse = UserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .name(u.getName())
                .role(u.getRole())
                .build();

        MovieResponse movieResponse = MovieResponse.builder()
                .id(m.getId())
                .title(m.getTitle())
                .description(m.getDescription())
                .duration(m.getDuration())
                .language(m.getLanguage())
                .genre(m.getGenre())
                .releaseDate(m.getReleaseDate())
                .status(m.getStatus())
                .build();

        TheatreResponse theatreResponse = TheatreResponse.builder()
                .id(t.getId())
                .name(t.getName())
                .location(t.getLocation())
                .capacity(t.getCapacity())
                .status(t.getStatus())
                .build();

        ShowResponse showResponse = ShowResponse.builder()
                .id(s.getId())
                .movie(movieResponse)
                .theatre(theatreResponse)
                .showDate(s.getShowDate())
                .showTime(s.getShowTime())
                .ticketPrice(s.getTicketPrice())
                .status(s.getStatus())
                .build();

        Long paymentId = booking.getPayment() != null ? booking.getPayment().getId() : null;
        PaymentStatus paymentStatus = booking.getPayment() != null ? booking.getPayment().getStatus() : null;

        return BookingResponse.builder()
                .id(booking.getId())
                .user(userResponse)
                .show(showResponse)
                .seatNumbers(booking.getSeatNumbers())
                .numberOfTickets(booking.getNumberOfTickets())
                .totalAmount(booking.getTotalAmount())
                .bookingDate(booking.getBookingDate())
                .status(booking.getStatus())
                .paymentId(paymentId)
                .paymentStatus(paymentStatus)
                .build();
    }
}
