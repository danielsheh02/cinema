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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Test
void deleteMovie_ShouldNotifyAndDelete_WhenMovieExists() {
    Movie movie = new Movie();
    movie.setId(1L);
    movie.setName("Test Movie");

    when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
    doNothing().when(movieRepository).deleteById(1L);
    doNothing().when(cinemaBot).notifyDeleteMovie(movie.getName());

    movieService.deleteMovie(1L);

    verify(movieRepository).deleteById(1L);
    verify(cinemaBot).notifyDeleteMovie("Test Movie");
}

@Test
void deleteMovie_ShouldDeleteWithoutNotify_WhenMovieDoesNotExist() {
    when(movieRepository.findById(1L)).thenReturn(Optional.empty());
    doNothing().when(movieRepository).deleteById(1L);

    movieService.deleteMovie(1L);

    verify(movieRepository).deleteById(1L);
    verify(cinemaBot, never()).notifyDeleteMovie(anyString());
}

@Test
void getAllMovies_ShouldReturnListOfMovies() {
    Movie movie1 = new Movie();
    movie1.setId(1L);
    movie1.setName("Movie 1");

    Movie movie2 = new Movie();
    movie2.setId(2L);
    movie2.setName("Movie 2");

    when(movieRepository.findAll()).thenReturn(List.of(movie1, movie2));

    List<Movie> movies = movieService.getAllMovies();

    assertNotNull(movies);
    assertEquals(2, movies.size());
    assertEquals("Movie 1", movies.get(0).getName());
}

@Test
void getMovie_ShouldReturnMovie_WhenFound() {
    Movie movie = new Movie();
    movie.setId(1L);
    movie.setName("Movie 1");

    when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

    Movie result = movieService.getMovie(1L);

    assertNotNull(result);
    assertEquals("Movie 1", result.getName());
}

@Test
void getMovie_ShouldReturnNull_WhenNotFound() {
    when(movieRepository.findById(1L)).thenReturn(Optional.empty());

    Movie result = movieService.getMovie(1L);

    assertNull(result);
}

@Test
void getAllMoviesById_ShouldReturnSetOfMovies() {
    Movie movie1 = new Movie();
    movie1.setId(1L);
    movie1.setName("Movie 1");

    Movie movie2 = new Movie();
    movie2.setId(2L);
    movie2.setName("Movie 2");

    List<Long> ids = List.of(1L, 2L);

    when(movieRepository.findAllById(ids)).thenReturn(Set.of(movie1, movie2));

    Set<Movie> movies = movieService.getAllMoviesById(ids);

    assertNotNull(movies);
    assertEquals(2, movies.size());
}
}

