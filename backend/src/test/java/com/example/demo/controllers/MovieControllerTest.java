package com.example.demo.controllers;

import com.app.cinema.bot.CinemaBot;
import com.app.cinema.controllers.MovieController;
import com.app.cinema.controllers.requestbody.MovieRequest;
import com.app.cinema.models.Movie;
import com.app.cinema.services.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MovieControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MovieService movieService;

    @Mock
    private CinemaBot cinemaBot;

    @InjectMocks
    private MovieController movieController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(movieController).build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testGetAllMoviesReturnsOk() throws Exception {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setName("Inception");
        movie.setTimeDuration(Duration.ofMinutes(148));

        Mockito.when(movieService.getAllMovies()).thenReturn(List.of(movie));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Inception"));
    }

    @Test
    void testCreateMovieReturnsMovie() throws Exception {
        MovieRequest request = new MovieRequest("Interstellar", Duration.ofMinutes(169));
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setName("Interstellar");
        movie.setTimeDuration(Duration.ofMinutes(169));

        Mockito.when(movieService.createMovie(any())).thenReturn(movie);

        mockMvc.perform(post("/api/movies/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Interstellar"));
    }

    @Test
void testGetMovieReturnsMovie() throws Exception {
    Movie movie = new Movie();
    movie.setId(1L);
    movie.setName("Inception");
    movie.setTimeDuration(Duration.ofMinutes(148));

    Mockito.when(movieService.getMovie(1L)).thenReturn(movie);

    mockMvc.perform(get("/api/movies/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("Inception"));
}

@Test
void testGetMovieReturnsNotFound() throws Exception {
    Mockito.when(movieService.getMovie(1L)).thenReturn(null);

    mockMvc.perform(get("/api/movies/1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Не найдено"));
}

@Test
void testGetMovieThrowsException() throws Exception {
    Mockito.when(movieService.getMovie(1L)).thenThrow(new RuntimeException("error"));

    mockMvc.perform(get("/api/movies/1"))
            .andExpect(status().isInternalServerError());
}

@Test
void testUpdateMovieReturnsUpdatedMovie() throws Exception {
    MovieRequest request = new MovieRequest("Inception Updated", Duration.ofMinutes(150));
    Movie updatedMovie = new Movie();
    updatedMovie.setId(1L);
    updatedMovie.setName("Inception Updated");
    updatedMovie.setTimeDuration(Duration.ofMinutes(150));

    Mockito.when(movieService.updateMovie(Mockito.eq(1L), any(MovieRequest.class))).thenReturn(updatedMovie);

    mockMvc.perform(put("/api/movies/admin/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Inception Updated"));
}

@Test
void testUpdateMovieThrowsException() throws Exception {
    MovieRequest request = new MovieRequest("Inception Updated", Duration.ofMinutes(150));

    Mockito.when(movieService.updateMovie(Mockito.eq(1L), any(MovieRequest.class))).thenThrow(new RuntimeException("error"));

    mockMvc.perform(put("/api/movies/admin/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError());
}

    @Test
    void testDeleteMovieReturnsOk() throws Exception {
        Mockito.doNothing().when(movieService).deleteMovie(1L);

        mockMvc.perform(delete("/api/movies/admin/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteMovieThrowsException() throws Exception {
        Mockito.doThrow(new RuntimeException("error")).when(movieService).deleteMovie(1L);

        mockMvc.perform(delete("/api/movies/admin/1"))
                .andExpect(status().isInternalServerError());
    }
}