package com.atquil.seatsagaplatform.controller;

import com.atquil.seatsagaplatform.dto.ShowSeatUpdateRequest;
import com.atquil.seatsagaplatform.entity.ShowSeat;
import com.atquil.seatsagaplatform.service.ShowSeatService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author atquil
 */
//@RestController
//@RequestMapping("/api/shows/{showId}/seats")
//@RequiredArgsConstructor
//public class ShowSeatManagementController {
//
//    private final ShowSeatService showSeatService;
//
//    @GetMapping
//    @Operation(summary = "Get all seats inventory for a show")
//    public List<ShowSeat> getSeats(@PathVariable Long showId) {
//        return showSeatService.getByShow(showId);
//    }
//
//    @PatchMapping("/{showSeatId}")
//    @Operation(summary = "Update status & price of a single show seat")
//    public ShowSeat updateSeat(@PathVariable Long showId,
//                               @PathVariable Long showSeatId,
//                               @Valid @RequestBody ShowSeatUpdateRequest request) {
//        return showSeatService.updateShowSeat(showSeatId, request);
//    }
//
//}
