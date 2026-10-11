package com.ijse.moviebooking.service;

import com.ijse.moviebooking.dto.request.MovieRequest;
import com.ijse.moviebooking.dto.response.MovieResponse;
import com.ijse.moviebooking.enums.MovieStatus;

import java.util.List;

public interface MovieService {
    MovieResponse createMovie(MovieRequest movieRequest);
    List<MovieResponse> getAllMovies(String title, String language, MovieStatus status);
    MovieResponse getMovieById(Long id);
    MovieResponse updateMovie(Long id, MovieRequest movieRequest);
    void deleteMovie(Long id);
}
