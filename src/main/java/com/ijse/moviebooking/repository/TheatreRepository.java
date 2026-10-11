package com.ijse.moviebooking.repository;

import com.ijse.moviebooking.entity.Theatre;
import com.ijse.moviebooking.enums.TheatreStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {

    List<Theatre> findByStatus(TheatreStatus status);
}
