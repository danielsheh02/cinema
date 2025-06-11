package com.app.cinema.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.cinema.bot.CinemaBot;
import com.app.cinema.controllers.requestbody.MovieRequest;
import com.app.cinema.models.Movie;
import com.app.cinema.repositories.MovieRepository;

@Service
public class MovieService {
    @Autowired
    MovieRepository movieRepository;

    @Autowired
    CinemaBot cinemaBot;

    public List<Movie> getAllMovies() {
        return (List<Movie>) movieRepository.findAll();
    }

    public Movie getMovie(Long id) {
        return movieRepository.findById(id).orElse(null);
    }

    public Set<Movie> getAllMoviesById(List<Long> listMoviesId) {
        return movieRepository.findAllById(listMoviesId);
    }

    public Movie createMovie(MovieRequest movieRequest) {
        Movie movie = new Movie();
        movie.setName(movieRequest.getName());
        movie.setTimeDuration(movieRequest.getTimeDuration());
        Movie saved = movieRepository.save(movie);

        cinemaBot.notifyNewMovie(saved.getName());
    
        return saved;
    }

    public Movie updateMovie(Long id, MovieRequest movieRequest) {
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Movie not found with id " + id));

        cinemaBot.notifyUpdateMovie(movie.getName(), movieRequest.getName());    
    
        movie.setName(movieRequest.getName());
        movie.setTimeDuration(movieRequest.getTimeDuration());
    
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id).orElse(null);
        if (movie != null) {
            cinemaBot.notifyDeleteMovie(movie.getName());
        }
        movieRepository.deleteById(id);
    }

}
