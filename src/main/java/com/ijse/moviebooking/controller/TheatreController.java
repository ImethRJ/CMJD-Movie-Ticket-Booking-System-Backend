package com.ijse.moviebooking.controller;

import com.ijse.moviebooking.dto.request.TheatreRequest;
import com.ijse.moviebooking.dto.response.MessageResponse;
import com.ijse.moviebooking.dto.response.TheatreResponse;
import com.ijse.moviebooking.service.TheatreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theatres")
@RequiredArgsConstructor
public class TheatreController {

    private final TheatreService theatreService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TheatreResponse> createTheatre(@Valid @RequestBody TheatreRequest theatreRequest) {
        TheatreResponse createdTheatre = theatreService.createTheatre(theatreRequest);
        return new ResponseEntity<>(createdTheatre, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TheatreResponse>> getTheatres() {
        return ResponseEntity.ok(theatreService.getAllTheatres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheatreResponse> getTheatreById(@PathVariable Long id) {
        return ResponseEntity.ok(theatreService.getTheatreById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TheatreResponse> updateTheatre(
            @PathVariable Long id,
            @Valid @RequestBody TheatreRequest theatreRequest
    ) {
        TheatreResponse updatedTheatre = theatreService.updateTheatre(id, theatreRequest);
        return ResponseEntity.ok(updatedTheatre);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteTheatre(@PathVariable Long id) {
        theatreService.deleteTheatre(id);
        return ResponseEntity.ok(new MessageResponse("Theatre with id " + id + " deleted successfully"));
    }
}
