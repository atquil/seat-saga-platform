package com.atquil.seatsagaplatform.service;

import com.atquil.seatsagaplatform.constants.SeatStatus;
import com.atquil.seatsagaplatform.dto.SeatDTO;
import com.atquil.seatsagaplatform.dto.ShowSeatUpdateRequest;
import com.atquil.seatsagaplatform.entity.ShowSeat;
import com.atquil.seatsagaplatform.repo.ShowSeatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author atquil
 */
@Service
@RequiredArgsConstructor
public class ShowSeatService {

    private final ShowSeatRepository showSeatRepository;

    @Transactional(readOnly = true)
    public List<ShowSeat> getByShow(Long showId) {
        return showSeatRepository.findByShowId(showId);
    }

    @Transactional(readOnly = true)
    public List<SeatDTO> getSeatsForShow(Long showId) {
        List<ShowSeat> showSeats = showSeatRepository.findByShowId(showId);
        return showSeats.stream()
                .map(ss -> new SeatDTO(
                        ss.getId(),
                        ss.getSeat().getId(),
                        ss.getSeat().getRowNumber(),
                        ss.getSeat().getSeatNumber(),
                        ss.getStatus(),
                        ss.getPrice()  // Make sure SeatDTO has price field
                ))
                .toList();
    }


    @Transactional
    public ShowSeat updateShowSeat(Long showSeatId, ShowSeatUpdateRequest request) {
        ShowSeat seat = showSeatRepository.findById(showSeatId)
                .orElseThrow(() -> new EntityNotFoundException("ShowSeat not found"));

        // Business rule: only allow update if not already booked
        if (seat.getStatus() == SeatStatus.BOOKED) {
            throw new IllegalStateException("Cannot modify booked seat");
        }

        seat.setStatus(request.status());
        seat.setPrice(request.price());

        return showSeatRepository.save(seat);
    }
}
