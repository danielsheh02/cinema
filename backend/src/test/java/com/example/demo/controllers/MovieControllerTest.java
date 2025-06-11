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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}