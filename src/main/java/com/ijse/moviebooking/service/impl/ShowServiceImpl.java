package com.ijse.moviebooking.service.impl;

import com.ijse.moviebooking.dto.request.ShowRequest;
import com.ijse.moviebooking.dto.response.MovieResponse;
import com.ijse.moviebooking.dto.response.ShowResponse;
import com.ijse.moviebooking.dto.response.ShowSeatAvailabilityResponse;
import com.ijse.moviebooking.dto.response.TheatreResponse;
import com.ijse.moviebooking.entity.Movie;
import com.ijse.moviebooking.entity.Show;
import com.ijse.moviebooking.entity.Theatre;
import com.ijse.moviebooking.enums.BookingStatus;
import com.ijse.moviebooking.enums.MovieStatus;
import com.ijse.moviebooking.enums.TheatreStatus;
import com.ijse.moviebooking.exception.BadRequestException;
import com.ijse.moviebooking.exception.ResourceNotFoundException;
import com.ijse.moviebooking.repository.BookingRepository;
import com.ijse.moviebooking.repository.MovieRepository;
import com.ijse.moviebooking.repository.ShowRepository;
import com.ijse.moviebooking.repository.TheatreRepository;
import com.ijse.moviebooking.service.ShowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowServiceImpl implements ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ShowResponse createShow(ShowRequest showRequest) {
        log.info("Creating show for movie ID: {} and theatre ID: {}", showRequest.getMovieId(), showRequest.getTheatreId());

        Movie movie = movieRepository.findById(showRequest.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + showRequest.getMovieId()));

        if (movie.getStatus() == MovieStatus.ENDED) {
            throw new BadRequestException("Cannot create a show for a movie with ENDED status");
        }

        Theatre theatre = theatreRepository.findById(showRequest.getTheatreId())
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + showRequest.getTheatreId()));

        if (theatre.getStatus() != TheatreStatus.ACTIVE) {
            throw new BadRequestException("Shows can only be created for an ACTIVE theatre");
        }

        List<Show> conflictingShows = showRepository.findConflictingShows(
                theatre.getId(), showRequest.getShowDate(), showRequest.getShowTime());
        if (!conflictingShows.isEmpty()) {
            throw new BadRequestException("The theatre already has a conflicting show scheduled at the same date and time");
        }

        Show show = Show.builder()
                .movie(movie)
                .theatre(theatre)
                .showDate(showRequest.getShowDate())
                .showTime(showRequest.getShowTime())
                .ticketPrice(showRequest.getTicketPrice())
                .status(showRequest.getStatus())
                .build();

        Show savedShow = showRepository.save(show);
        return mapToShowResponse(savedShow);
    }

    @Override
    public List<ShowResponse> getAllShows() {
        log.info("Fetching all shows");
        return showRepository.findAll().stream()
                .map(this::mapToShowResponse)
                .toList();
    }

    @Override
    public ShowResponse getShowById(Long id) {
        log.info("Fetching show by ID: {}", id);
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + id));
        return mapToShowResponse(show);
    }

    @Override
    @Transactional
    public ShowResponse updateShow(Long id, ShowRequest showRequest) {
        log.info("Updating show ID: {}", id);
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + id));

        Movie movie = movieRepository.findById(showRequest.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + showRequest.getMovieId()));

        if (movie.getStatus() == MovieStatus.ENDED) {
            throw new BadRequestException("Cannot assign a movie with ENDED status to a show");
        }

        Theatre theatre = theatreRepository.findById(showRequest.getTheatreId())
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + showRequest.getTheatreId()));

        if (theatre.getStatus() != TheatreStatus.ACTIVE) {
            throw new BadRequestException("Shows can only be created/updated for an ACTIVE theatre");
        }

        List<Show> conflictingShows = showRepository.findConflictingShows(
                theatre.getId(), showRequest.getShowDate(), showRequest.getShowTime());
        boolean hasConflict = conflictingShows.stream().anyMatch(s -> !s.getId().equals(id));
        if (hasConflict) {
            throw new BadRequestException("The theatre already has a conflicting show scheduled at the same date and time");
        }

        show.setMovie(movie);
        show.setTheatre(theatre);
        show.setShowDate(showRequest.getShowDate());
        show.setShowTime(showRequest.getShowTime());
        show.setTicketPrice(showRequest.getTicketPrice());
        show.setStatus(showRequest.getStatus());

        Show updatedShow = showRepository.save(show);
        return mapToShowResponse(updatedShow);
    }

    @Override
    @Transactional
    public void deleteShow(Long id) {
        log.info("Deleting show ID: {}", id);
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + id));
        showRepository.delete(show);
    }

    @Override
    public List<ShowResponse> getShowsByMovieId(Long movieId) {
        log.info("Fetching shows for movie ID: {}", movieId);
        if (!movieRepository.existsById(movieId)) {
            throw new ResourceNotFoundException("Movie not found with id: " + movieId);
        }
        return showRepository.findByMovieId(movieId).stream()
                .map(this::mapToShowResponse)
                .toList();
    }

    @Override
    public ShowSeatAvailabilityResponse getShowSeatAvailability(Long showId) {
        log.info("Fetching seat availability for show ID: {}", showId);
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + showId));

        List<String> bookedSeats = bookingRepository.findBookedSeatsByShowIdAndStatuses(
                showId, List.of(BookingStatus.CONFIRMED, BookingStatus.PENDING));

        int totalCapacity = show.getTheatre().getCapacity();
        int bookedCount = bookedSeats.size();
        int availableCount = Math.max(0, totalCapacity - bookedCount);

        return ShowSeatAvailabilityResponse.builder()
                .showId(show.getId())
                .movieTitle(show.getMovie().getTitle())
                .theatreName(show.getTheatre().getName())
                .totalCapacity(totalCapacity)
                .bookedSeats(bookedSeats)
                .bookedSeatsCount(bookedCount)
                .availableSeatsCount(availableCount)
                .build();
    }

    private ShowResponse mapToShowResponse(Show show) {
        Movie m = show.getMovie();
        Theatre t = show.getTheatre();

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

        return ShowResponse.builder()
                .id(show.getId())
                .movie(movieResponse)
                .theatre(theatreResponse)
                .showDate(show.getShowDate())
                .showTime(show.getShowTime())
                .ticketPrice(show.getTicketPrice())
                .status(show.getStatus())
                .build();
    }
}
