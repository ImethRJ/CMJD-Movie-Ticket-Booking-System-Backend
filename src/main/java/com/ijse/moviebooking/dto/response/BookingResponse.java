package com.ijse.moviebooking.dto.response;

import com.ijse.moviebooking.enums.BookingStatus;
import com.ijse.moviebooking.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private UserResponse user;
    private ShowResponse show;
    private List<String> seatNumbers;
    private Integer numberOfTickets;
    private BigDecimal totalAmount;
    private LocalDateTime bookingDate;
    private BookingStatus status;
    private Long paymentId;
    private PaymentStatus paymentStatus;
}
