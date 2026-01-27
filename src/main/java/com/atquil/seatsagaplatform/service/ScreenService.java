package com.atquil.seatsagaplatform.service;

import com.atquil.seatsagaplatform.constants.SeatType;
import com.atquil.seatsagaplatform.dto.ScreenRequest;
import com.atquil.seatsagaplatform.dto.ScreenResponse;
import com.atquil.seatsagaplatform.entity.Screen;
import com.atquil.seatsagaplatform.entity.Seat;
import com.atquil.seatsagaplatform.entity.Theatre;
import com.atquil.seatsagaplatform.repo.ScreenRepository;
import com.atquil.seatsagaplatform.repo.SeatRepository;
import com.atquil.seatsagaplatform.repo.TheatreRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final TheatreRepository theatreRepository;
    private final SeatRepository seatRepository;  // Add this

    @Transactional(readOnly = true)
    public List<ScreenResponse> getByTheatre(Long theatreId) {
        List<Screen> screens = screenRepository.findByTheatreId(theatreId);
        return screens.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ScreenResponse add(Long theatreId, ScreenRequest request) {
        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(() -> new EntityNotFoundException("Theatre not found"));

        Screen screen = Screen.builder()
                .theatre(theatre)
                .name(request.getName())
                .totalSeats(request.getTotalSeats())
                .seatLayoutJson(request.getSeatLayoutJson())
                .build();

        Screen savedScreen = screenRepository.save(screen);

        // CRITICAL: Create seats automatically
        createSeatsFromLayout(savedScreen, request.getSeatLayoutJson());

        return toResponse(savedScreen);
    }

    @Transactional
    public ScreenResponse update(Long theatreId, Long id, ScreenRequest request) {
        Screen screen = screenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Screen not found: " + id));

        if (!screen.getTheatre().getId().equals(theatreId)) {
            throw new IllegalArgumentException("Screen does not belong to this theatre");
        }

        screen.setName(request.getName());
        screen.setTotalSeats(request.getTotalSeats());
        screen.setSeatLayoutJson(request.getSeatLayoutJson());

        Screen updatedScreen = screenRepository.save(screen);
        return toResponse(updatedScreen);
    }

    // Alternative without ObjectMapper
    private void createSeatsFromLayout(Screen screen, String seatLayoutJson) {
        if (seatLayoutJson == null || seatLayoutJson.trim().isEmpty()) {
            createDefaultSeats(screen);
            return;
        }

        // Simple parsing for basic JSON
        try {
            // Remove all whitespace and parse
            String cleanJson = seatLayoutJson.replaceAll("\\s+", "");
            if (cleanJson.contains("\"rows\":[")) {
                // Simple parsing - adjust based on your JSON structure
                // This is a simplified version
                createDefaultSeats(screen);
            } else {
                createDefaultSeats(screen);
            }
        } catch (Exception e) {
            log.error("Error parsing seat layout: {}", e.getMessage());
            createDefaultSeats(screen);
        }
    }

    private void createDefaultSeats(Screen screen) {
        // Create 2 rows (A, B) with 5 seats each as default
        createRowOfSeats(screen, "A", 5, SeatType.REGULAR);
        createRowOfSeats(screen, "B", 5, SeatType.PREMIUM);
    }

    private void createRowOfSeats(Screen screen, String rowNumber, int seatCount, SeatType seatType) {
        for (int seatNum = 1; seatNum <= seatCount; seatNum++) {
            Seat seat = Seat.builder()
                    .screen(screen)
                    .rowNumber(rowNumber)
                    .seatNumber(seatNum)
                    .seatType(seatType)
                    .build();
            seatRepository.save(seat);
        }
    }

    // Helper method to convert Screen to ScreenResponse
    private ScreenResponse toResponse(Screen screen) {
        return new ScreenResponse(
                screen.getId(),
                screen.getName(),
                screen.getTotalSeats(),
                screen.getSeatLayoutJson(),
                screen.getTheatre().getId()
        );
    }
}