package com.atquil.seatsagaplatform.controller;

import com.atquil.seatsagaplatform.dto.*;
import com.atquil.seatsagaplatform.entity.Show;
import com.atquil.seatsagaplatform.entity.ShowSeat;
import com.atquil.seatsagaplatform.entity.Theatre;
import com.atquil.seatsagaplatform.service.ShowSeatService;
import com.atquil.seatsagaplatform.service.ShowService;
import com.atquil.seatsagaplatform.service.TheatreService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author atquil
 */
@RestController
@RequestMapping("/api/partners/{partnerId}/theatres")
@RequiredArgsConstructor
public class TheatreController {

    private final TheatreService theatreService;
    private final ShowService showService;

    @GetMapping
    @Operation(summary = "List theatres by partner")
    public List<TheatreResponse> getByPartner(@PathVariable Long partnerId) {
        return theatreService.getByPartner(partnerId);
    }
    @PostMapping
    @Operation(summary = "Add new theatre")
    public ResponseEntity<Theatre> add(@PathVariable Long partnerId, @Valid @RequestBody TheatreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(theatreService.add(partnerId, request));
    }

    @GetMapping("/{theatreId}/shows")
    public List<ShowResponse> getShows(@PathVariable Long partnerId, @PathVariable Long theatreId) {
        theatreService.getTheatre(partnerId, theatreId); // just for validation
        return showService.getShowsByTheatre(theatreId);
    }

    @PostMapping("/{theatreId}/screens/{screenId}/shows")
    public ResponseEntity<ShowResponse> createShow(@PathVariable Long partnerId,
                                                   @PathVariable Long theatreId,
                                                   @PathVariable Long screenId,
                                                   @Valid @RequestBody ShowRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(showService.createShow(partnerId, theatreId, screenId, request));
    }


    @PutMapping("/{theatreId}/shows/{showId}")
    public ShowResponse updateShow(@PathVariable Long partnerId,
                                   @PathVariable Long theatreId,
                                   @PathVariable Long showId,
                                   @Valid @RequestBody ShowRequest request) {
        return showService.updateShow(partnerId, theatreId, showId, request);
    }

    @DeleteMapping("/{theatreId}/shows/{showId}")
    public ResponseEntity<Void> deleteShow(@PathVariable Long partnerId,
                                           @PathVariable Long theatreId,
                                           @PathVariable Long showId) {
        showService.deleteShow(partnerId, theatreId, showId);
        return ResponseEntity.noContent().build();
    }

    // Seat Inventory
    @GetMapping("/{theatreId}/shows/{showId}/seats")
    public List<ShowSeat> getSeatInventory(@PathVariable Long partnerId,
                                           @PathVariable Long theatreId,
                                           @PathVariable Long showId) {
        theatreService.getTheatre(partnerId, theatreId); // validation
        return showService.getShowSeatInventory(showId);
    }

    @PatchMapping("/{theatreId}/shows/{showId}/seats/{showSeatId}")
    public ShowSeat updateSeat(@PathVariable Long partnerId,
                               @PathVariable Long theatreId,
                               @PathVariable Long showId,
                               @PathVariable Long showSeatId,
                               @Valid @RequestBody ShowSeatUpdateRequest request) {
        theatreService.getTheatre(partnerId, theatreId); // validation
        return showService.updateShowSeat(showId, showSeatId, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update theatre")
    public Theatre update(@PathVariable Long partnerId, @PathVariable Long id, @Valid @RequestBody TheatreRequest request) {
        return theatreService.update(partnerId, id, request);
    }

}
