package com.atquil.seatsagaplatform.controller;

import com.atquil.seatsagaplatform.dto.MovieRequest;
import com.atquil.seatsagaplatform.dto.MovieResponse;
import com.atquil.seatsagaplatform.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    @Operation(summary = "List all movies")
    public List<MovieResponse> getAll() {
        return movieService.getAll();
    }

    @PostMapping
    @Operation(summary = "Add new movie")
    public ResponseEntity<MovieResponse> add(@Valid @RequestBody MovieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.add(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update movie")
    public MovieResponse update(@PathVariable Long id, @Valid @RequestBody MovieRequest request) {
        return movieService.update(id, request);
    }
}