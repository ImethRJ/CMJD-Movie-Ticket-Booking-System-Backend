package com.ijse.moviebooking.service.impl;

import com.ijse.moviebooking.dto.request.MovieRequest;
import com.ijse.moviebooking.dto.response.MovieResponse;
import com.ijse.moviebooking.entity.Movie;
import com.ijse.moviebooking.enums.MovieStatus;
import com.ijse.moviebooking.exception.ResourceNotFoundException;
import com.ijse.moviebooking.repository.MovieRepository;
import com.ijse.moviebooking.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    @Transactional
    public MovieResponse createMovie(MovieRequest movieRequest) {
        log.info("Creating movie with title: {}", movieRequest.getTitle());
        Movie movie = Movie.builder()
                .title(movieRequest.getTitle())
                .description(movieRequest.getDescription())
                .duration(movieRequest.getDuration())
                .language(movieRequest.getLanguage())
                .genre(movieRequest.getGenre())
                .releaseDate(movieRequest.getReleaseDate())
                .status(movieRequest.getStatus())
                .posterUrl(movieRequest.getPosterUrl())
                .build();

        Movie savedMovie = movieRepository.save(movie);
        return mapToMovieResponse(savedMovie);
    }

    @Override
    public List<MovieResponse> getAllMovies(String title, String language, MovieStatus status) {
        log.info("Fetching movies with title filter: {}, language filter: {}, status filter: {}", title, language, status);
        Specification<Movie> spec = Specification.where(null);

        if (title != null && !title.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if (language != null && !language.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("language")), language.toLowerCase()));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        return movieRepository.findAll(spec).stream()
                .map(this::mapToMovieResponse)
                .toList();
    }

    @Override
    public MovieResponse getMovieById(Long id) {
        log.info("Fetching movie by ID: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        return mapToMovieResponse(movie);
    }

    @Override
    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest movieRequest) {
        log.info("Updating movie ID: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        movie.setTitle(movieRequest.getTitle());
        movie.setDescription(movieRequest.getDescription());
        movie.setDuration(movieRequest.getDuration());
        movie.setLanguage(movieRequest.getLanguage());
        movie.setGenre(movieRequest.getGenre());
        movie.setReleaseDate(movieRequest.getReleaseDate());
        movie.setStatus(movieRequest.getStatus());
        movie.setPosterUrl(movieRequest.getPosterUrl());

        Movie updatedMovie = movieRepository.save(movie);
        return mapToMovieResponse(updatedMovie);
    }

    @Override
    @Transactional
    public void deleteMovie(Long id) {
        log.info("Deleting movie ID: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        movieRepository.delete(movie);
    }

    private MovieResponse mapToMovieResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .duration(movie.getDuration())
                .language(movie.getLanguage())
                .genre(movie.getGenre())
                .releaseDate(movie.getReleaseDate())
                .status(movie.getStatus())
                .posterUrl(movie.getPosterUrl())
                .build();
    }
}
