package com.atquil.seatsagaplatform.service;

import com.atquil.seatsagaplatform.dto.MovieRequest;
import com.atquil.seatsagaplatform.dto.MovieResponse;
import com.atquil.seatsagaplatform.entity.Movie;
import com.atquil.seatsagaplatform.repo.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    @Transactional(readOnly = true)
    public List<MovieResponse> getAll() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MovieResponse add(MovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.title())
                .description(request.description())
                .durationMinutes(request.durationMinutes())
                .genre(request.genre())
                .build();

        Movie savedMovie = movieRepository.save(movie);
        return toResponse(savedMovie);
    }

    @Transactional
    public MovieResponse update(Long id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found: " + id));

        movie.setTitle(request.title());
        movie.setDescription(request.description());
        movie.setDurationMinutes(request.durationMinutes());
        movie.setGenre(request.genre());

        Movie updatedMovie = movieRepository.save(movie);
        return toResponse(updatedMovie);
    }

    // Helper method to convert Movie to MovieResponse
    private MovieResponse toResponse(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getDurationMinutes(),
                movie.getGenre()
        );
    }
}