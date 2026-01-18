package com.atquil.seatsagaplatform.controller;

import com.atquil.seatsagaplatform.dto.ShowDetailResponse;
import com.atquil.seatsagaplatform.dto.ShowSearchResponse;
import com.atquil.seatsagaplatform.service.ShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @author atquil
 */
@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
@Tag(name = "Show Discovery", description = "Browse shows by city and movie")
public class ShowController {

    private final ShowService showService;

    @GetMapping("/search")
    @Operation(summary = "Search Shows", description = "Finds all theatres running a specific movie in a city on a specific date.")
    public ResponseEntity<List<ShowSearchResponse>> searchShows(
            @RequestParam String city,
            @RequestParam Long movieId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Default to today if no date provided
        if (date == null) {
            date = LocalDate.now();
        }

        return ResponseEntity.ok(showService.searchShows(city, movieId, date));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Show Details", description = "Fetch metadata for a specific show ID")
    public ResponseEntity<ShowDetailResponse> getShow(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getShow(id));
    }


    @GetMapping("/{id}/seats")
    @Operation(summary = "Get Seats for Show", description = "Fetches seat availability for a specific show")
    public ResponseEntity<?> getSeats(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getSeatsForShow(id));
    }
}
