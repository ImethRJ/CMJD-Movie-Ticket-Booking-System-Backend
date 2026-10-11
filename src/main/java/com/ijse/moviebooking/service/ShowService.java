package com.ijse.moviebooking.service;

import com.ijse.moviebooking.dto.request.ShowRequest;
import com.ijse.moviebooking.dto.response.ShowResponse;
import com.ijse.moviebooking.dto.response.ShowSeatAvailabilityResponse;

import java.util.List;

public interface ShowService {
    ShowResponse createShow(ShowRequest showRequest);
    List<ShowResponse> getAllShows();
    ShowResponse getShowById(Long id);
    ShowResponse updateShow(Long id, ShowRequest showRequest);
    void deleteShow(Long id);
    List<ShowResponse> getShowsByMovieId(Long movieId);
    ShowSeatAvailabilityResponse getShowSeatAvailability(Long showId);
}
