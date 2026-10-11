package com.ijse.moviebooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowSeatAvailabilityResponse {
    private Long showId;
    private String movieTitle;
    private String theatreName;
    private Integer totalCapacity;
    private List<String> bookedSeats;
    private Integer bookedSeatsCount;
    private Integer availableSeatsCount;
}
