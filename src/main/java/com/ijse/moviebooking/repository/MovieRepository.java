package com.ijse.moviebooking.repository;

import com.ijse.moviebooking.entity.Movie;
import com.ijse.moviebooking.enums.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

    List<Movie> findByStatus(MovieStatus status);

    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    List<Movie> findByLanguageIgnoreCase(String language);
}
