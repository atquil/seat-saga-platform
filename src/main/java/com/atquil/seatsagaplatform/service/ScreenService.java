package com.atquil.seatsagaplatform.service;

import com.atquil.seatsagaplatform.dto.ScreenRequest;
import com.atquil.seatsagaplatform.dto.ScreenResponse;
import com.atquil.seatsagaplatform.entity.Screen;
import com.atquil.seatsagaplatform.entity.Theatre;
import com.atquil.seatsagaplatform.repo.ScreenRepository;
import com.atquil.seatsagaplatform.repo.TheatreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final TheatreRepository theatreRepository;

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
                .name(request.name())
                .totalSeats(request.totalSeats())
                .seatLayoutJson(request.seatLayoutJson())
                .build();

        Screen savedScreen = screenRepository.save(screen);
        return toResponse(savedScreen);
    }

    @Transactional
    public ScreenResponse update(Long theatreId, Long id, ScreenRequest request) {
        Screen screen = screenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Screen not found: " + id));

        if (!screen.getTheatre().getId().equals(theatreId)) {
            throw new IllegalArgumentException("Screen does not belong to this theatre");
        }

        screen.setName(request.name());
        screen.setTotalSeats(request.totalSeats());
        screen.setSeatLayoutJson(request.seatLayoutJson());

        Screen updatedScreen = screenRepository.save(screen);
        return toResponse(updatedScreen);
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