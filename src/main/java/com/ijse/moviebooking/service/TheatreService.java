package com.ijse.moviebooking.service;

import com.ijse.moviebooking.dto.request.TheatreRequest;
import com.ijse.moviebooking.dto.response.TheatreResponse;

import java.util.List;

public interface TheatreService {
    TheatreResponse createTheatre(TheatreRequest theatreRequest);
    List<TheatreResponse> getAllTheatres();
    TheatreResponse getTheatreById(Long id);
    TheatreResponse updateTheatre(Long id, TheatreRequest theatreRequest);
    void deleteTheatre(Long id);
}
