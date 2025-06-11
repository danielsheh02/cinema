package com.example.demo.services;

import com.app.cinema.bot.CinemaBot;
import com.app.cinema.controllers.requestbody.MovieRequest;
import com.app.cinema.models.Movie;
import com.app.cinema.repositories.MovieRepository;
import com.app.cinema.services.MovieService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @Mock
    private CinemaBot cinemaBot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMovie_ShouldReturnSavedMovie() {
        MovieRequest request = new MovieRequest("Matrix", Duration.ofMinutes(136));
        Movie saved = new Movie();
        saved.setId(1L);
        saved.setName("Matrix");
        saved.setTimeDuration(Duration.ofMinutes(136));

        when(movieRepository.save(any(Movie.class))).thenReturn(saved);

        Movie result = movieService.createMovie(request);

        assertEquals("Matrix", result.getName());
        assertEquals(Duration.ofMinutes(136), result.getTimeDuration());
    }

    @Test
    void updateMovie_ShouldUpdateAndReturnMovie() {
        Movie existing = new Movie();
        existing.setId(1L);
        existing.setName("Old");
        existing.setTimeDuration(Duration.ofMinutes(90));

        MovieRequest updatedRequest = new MovieRequest("New", Duration.ofMinutes(120));

        when(movieRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(movieRepository.save(any(Movie.class))).thenReturn(existing);

        Movie updated = movieService.updateMovie(1L, updatedRequest);

        assertEquals("New", updated.getName());
        assertEquals(Duration.ofMinutes(120), updated.getTimeDuration());
    }
}

