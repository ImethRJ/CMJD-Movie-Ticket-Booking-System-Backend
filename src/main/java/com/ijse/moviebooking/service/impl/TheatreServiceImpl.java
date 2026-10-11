package com.ijse.moviebooking.service.impl;

import com.ijse.moviebooking.dto.request.TheatreRequest;
import com.ijse.moviebooking.dto.response.TheatreResponse;
import com.ijse.moviebooking.entity.Theatre;
import com.ijse.moviebooking.exception.ResourceNotFoundException;
import com.ijse.moviebooking.repository.TheatreRepository;
import com.ijse.moviebooking.service.TheatreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheatreServiceImpl implements TheatreService {

    private final TheatreRepository theatreRepository;

    @Override
    @Transactional
    public TheatreResponse createTheatre(TheatreRequest theatreRequest) {
        log.info("Creating theatre: {}", theatreRequest.getName());
        Theatre theatre = Theatre.builder()
                .name(theatreRequest.getName())
                .location(theatreRequest.getLocation())
                .capacity(theatreRequest.getCapacity())
                .status(theatreRequest.getStatus())
                .build();

        Theatre savedTheatre = theatreRepository.save(theatre);
        return mapToTheatreResponse(savedTheatre);
    }

    @Override
    public List<TheatreResponse> getAllTheatres() {
        log.info("Fetching all theatres");
        return theatreRepository.findAll().stream()
                .map(this::mapToTheatreResponse)
                .toList();
    }

    @Override
    public TheatreResponse getTheatreById(Long id) {
        log.info("Fetching theatre by ID: {}", id);
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + id));
        return mapToTheatreResponse(theatre);
    }

    @Override
    @Transactional
    public TheatreResponse updateTheatre(Long id, TheatreRequest theatreRequest) {
        log.info("Updating theatre ID: {}", id);
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + id));

        theatre.setName(theatreRequest.getName());
        theatre.setLocation(theatreRequest.getLocation());
        theatre.setCapacity(theatreRequest.getCapacity());
        theatre.setStatus(theatreRequest.getStatus());

        Theatre updatedTheatre = theatreRepository.save(theatre);
        return mapToTheatreResponse(updatedTheatre);
    }

    @Override
    @Transactional
    public void deleteTheatre(Long id) {
        log.info("Deleting theatre ID: {}", id);
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + id));
        theatreRepository.delete(theatre);
    }

    private TheatreResponse mapToTheatreResponse(Theatre theatre) {
        return TheatreResponse.builder()
                .id(theatre.getId())
                .name(theatre.getName())
                .location(theatre.getLocation())
                .capacity(theatre.getCapacity())
                .status(theatre.getStatus())
                .build();
    }
}
