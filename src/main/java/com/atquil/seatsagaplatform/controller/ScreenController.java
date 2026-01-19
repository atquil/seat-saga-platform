package com.atquil.seatsagaplatform.controller;

import com.atquil.seatsagaplatform.dto.ScreenRequest;
import com.atquil.seatsagaplatform.dto.ScreenResponse;
import com.atquil.seatsagaplatform.service.ScreenService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theatres/{theatreId}/screens")
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenService screenService;

    @GetMapping
    @Operation(summary = "List screens by theatre")
    public List<ScreenResponse> getByTheatre(@PathVariable Long theatreId) {
        return screenService.getByTheatre(theatreId);
    }

    @PostMapping
    @Operation(summary = "Add new screen")
    public ResponseEntity<ScreenResponse> add(@PathVariable Long theatreId, @Valid @RequestBody ScreenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(screenService.add(theatreId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update screen")
    public ScreenResponse update(@PathVariable Long theatreId, @PathVariable Long id, @Valid @RequestBody ScreenRequest request) {
        return screenService.update(theatreId, id, request);
    }
}