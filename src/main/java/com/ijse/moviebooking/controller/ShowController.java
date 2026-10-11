package com.ijse.moviebooking.controller;

import com.ijse.moviebooking.dto.request.ShowRequest;
import com.ijse.moviebooking.dto.response.MessageResponse;
import com.ijse.moviebooking.dto.response.ShowResponse;
import com.ijse.moviebooking.dto.response.ShowSeatAvailabilityResponse;
import com.ijse.moviebooking.service.ShowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowResponse> createShow(@Valid @RequestBody ShowRequest showRequest) {
        ShowResponse createdShow = showService.createShow(showRequest);
        return new ResponseEntity<>(createdShow, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ShowResponse>> getShows() {
        return ResponseEntity.ok(showService.getAllShows());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowResponse> getShowById(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getShowById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowResponse> updateShow(
            @PathVariable Long id,
            @Valid @RequestBody ShowRequest showRequest
    ) {
        ShowResponse updatedShow = showService.updateShow(id, showRequest);
        return ResponseEntity.ok(updatedShow);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteShow(@PathVariable Long id) {
        showService.deleteShow(id);
        return ResponseEntity.ok(new MessageResponse("Show with id " + id + " deleted successfully"));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowResponse>> getShowsForMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(showService.getShowsByMovieId(movieId));
    }

    @GetMapping("/{id}/available-seats")
    public ResponseEntity<ShowSeatAvailabilityResponse> getShowSeatAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getShowSeatAvailability(id));
    }
}
