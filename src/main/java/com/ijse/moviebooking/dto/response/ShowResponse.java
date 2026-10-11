package com.ijse.moviebooking.dto.response;

import com.ijse.moviebooking.enums.ShowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowResponse {
    private Long id;
    private MovieResponse movie;
    private TheatreResponse theatre;
    private LocalDate showDate;
    private LocalTime showTime;
    private BigDecimal ticketPrice;
    private ShowStatus status;
}
