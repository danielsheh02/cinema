package com.app.cinema.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.cinema.controllers.requestbody.MovieRequest;
import com.app.cinema.models.Movie;
import com.app.cinema.security.AuthExc;
import com.app.cinema.services.MovieService;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    @Autowired
    MovieService movieService;

    @GetMapping("")
    public ResponseEntity<?> getAllMovies() {
        try {
            return ResponseEntity.ok().body(movieService.getAllMovies());
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovie(@PathVariable(name = "id") Long id) {
        try {
            Movie movie = movieService.getMovie(id);
            if (movie != null) {
                return ResponseEntity.ok().body(movie);
            } else {
                return new ResponseEntity<>(new AuthExc("Не найдено"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable Long id, @RequestBody MovieRequest movieRequest) {
        try {
            Movie updatedMovie = movieService.updateMovie(id, movieRequest);
            return ResponseEntity.ok().body(updatedMovie);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<?> createMovie(@RequestBody MovieRequest movieRequest) {
        try {
            Movie movie = movieService.createMovie(movieRequest);
            return ResponseEntity.ok().body(movie);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable(name = "id") Long id) {
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.ok().body("Удалено");
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
