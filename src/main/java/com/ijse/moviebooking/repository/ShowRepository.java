package com.ijse.moviebooking.repository;

import com.ijse.moviebooking.entity.Show;
import com.ijse.moviebooking.enums.ShowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    List<Show> findByMovieId(Long movieId);

    List<Show> findByTheatreId(Long theatreId);

    List<Show> findByShowDate(LocalDate showDate);

    @Query("SELECT s FROM Show s WHERE s.theatre.id = :theatreId AND s.showDate = :showDate AND s.showTime = :showTime AND s.status != 'CANCELLED'")
    List<Show> findConflictingShows(
            @Param("theatreId") Long theatreId,
            @Param("showDate") LocalDate showDate,
            @Param("showTime") LocalTime showTime
    );
}
