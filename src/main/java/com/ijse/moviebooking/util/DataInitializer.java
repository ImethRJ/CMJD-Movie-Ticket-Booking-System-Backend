package com.ijse.moviebooking.util;

import com.ijse.moviebooking.entity.Movie;
import com.ijse.moviebooking.entity.Show;
import com.ijse.moviebooking.entity.Theatre;
import com.ijse.moviebooking.entity.User;
import com.ijse.moviebooking.enums.MovieStatus;
import com.ijse.moviebooking.enums.Role;
import com.ijse.moviebooking.enums.ShowStatus;
import com.ijse.moviebooking.enums.TheatreStatus;
import com.ijse.moviebooking.repository.MovieRepository;
import com.ijse.moviebooking.repository.ShowRepository;
import com.ijse.moviebooking.repository.TheatreRepository;
import com.ijse.moviebooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final ShowRepository showRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Checking database data initialization...");

        if (userRepository.count() == 0) {
            log.info("Seeding initial users...");

            User admin = User.builder()
                    .username("admin")
                    .email("admin@moviebooking.com")
                    .password(passwordEncoder.encode("admin123"))
                    .name("System Administrator")
                    .role(Role.ADMIN)
                    .build();

            User customer1 = User.builder()
                    .username("imeth_j")
                    .email("imeth@example.com")
                    .password(passwordEncoder.encode("customer123"))
                    .name("Imeth Jayasinghe")
                    .role(Role.CUSTOMER)
                    .build();

            User customer2 = User.builder()
                    .username("sonal_i")
                    .email("sonal@example.com")
                    .password(passwordEncoder.encode("customer123"))
                    .name("Sonal Imsara")
                    .role(Role.CUSTOMER)
                    .build();

            userRepository.save(admin);
            userRepository.save(customer1);
            userRepository.save(customer2);
            log.info("Default users created successfully (ADMIN: admin/admin123, CUSTOMER: imeth_j/customer123, sonal_i/customer123)");
        }

        if (movieRepository.count() == 0) {
            log.info("Seeding initial movies...");

            Movie movie1 = Movie.builder()
                    .title("Avatar: The Way of Water")
                    .description("Jake Sully lives with his newfound family formed on the extrasolar moon Pandora.")
                    .duration(192)
                    .language("English")
                    .genre("Sci-Fi")
                    .releaseDate(LocalDate.of(2022, 12, 16))
                    .status(MovieStatus.NOW_SHOWING)
                    .posterUrl("https://image.tmdb.org/t/p/w500/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg")
                    .build();

            Movie movie2 = Movie.builder()
                    .title("Inception")
                    .description("A thief who steals corporate secrets through dream-sharing technology.")
                    .duration(148)
                    .language("English")
                    .genre("Action/Sci-Fi")
                    .releaseDate(LocalDate.of(2010, 7, 16))
                    .status(MovieStatus.NOW_SHOWING)
                    .posterUrl("https://image.tmdb.org/t/p/w500/edv5CZvWj09upOsy2Y6IwDhK8bt.jpg")
                    .build();

            Movie movie3 = Movie.builder()
                    .title("Deadpool & Wolverine")
                    .description("Wolverine recovers from his injuries when he crosses paths with Deadpool.")
                    .duration(127)
                    .language("English")
                    .genre("Action/Comedy")
                    .releaseDate(LocalDate.of(2024, 7, 26))
                    .status(MovieStatus.UPCOMING)
                    .posterUrl("https://image.tmdb.org/t/p/w500/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg")
                    .build();

            movieRepository.save(movie1);
            movieRepository.save(movie2);
            movieRepository.save(movie3);
            log.info("Sample movies created successfully");
        }

        if (theatreRepository.count() == 0) {
            log.info("Seeding initial theatres...");

            Theatre theatre1 = Theatre.builder()
                    .name("Scope Cinemas - CCC")
                    .location("Colombo 02, Sri Lanka")
                    .capacity(150)
                    .status(TheatreStatus.ACTIVE)
                    .build();

            Theatre theatre2 = Theatre.builder()
                    .name("Liberty Cinema")
                    .location("Colombo 03, Sri Lanka")
                    .capacity(100)
                    .status(TheatreStatus.ACTIVE)
                    .build();

            Theatre theatre3 = Theatre.builder()
                    .name("Majestic Cineplex")
                    .location("Colombo 04, Sri Lanka")
                    .capacity(120)
                    .status(TheatreStatus.ACTIVE)
                    .build();

            theatreRepository.save(theatre1);
            theatreRepository.save(theatre2);
            theatreRepository.save(theatre3);
            log.info("Sample theatres created successfully");
        }

        if (showRepository.count() == 0 && movieRepository.count() > 0 && theatreRepository.count() > 0) {
            log.info("Seeding initial shows...");

            Movie m1 = movieRepository.findAll().get(0);
            Theatre t1 = theatreRepository.findAll().get(0);
            Theatre t2 = theatreRepository.findAll().get(1);

            Show show1 = Show.builder()
                    .movie(m1)
                    .theatre(t1)
                    .showDate(LocalDate.now().plusDays(1))
                    .showTime(LocalTime.of(14, 30))
                    .ticketPrice(BigDecimal.valueOf(1500.00))
                    .status(ShowStatus.SCHEDULED)
                    .build();

            Show show2 = Show.builder()
                    .movie(m1)
                    .theatre(t2)
                    .showDate(LocalDate.now().plusDays(1))
                    .showTime(LocalTime.of(18, 00))
                    .ticketPrice(BigDecimal.valueOf(1200.00))
                    .status(ShowStatus.SCHEDULED)
                    .build();

            showRepository.save(show1);
            showRepository.save(show2);
            log.info("Sample shows created successfully");
        }
    }
}
