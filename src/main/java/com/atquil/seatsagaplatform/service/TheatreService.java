package com.atquil.seatsagaplatform.service;

import com.atquil.seatsagaplatform.constants.SeatStatus;
import com.atquil.seatsagaplatform.dto.ShowRequest;
import com.atquil.seatsagaplatform.dto.ShowSeatUpdateRequest;
import com.atquil.seatsagaplatform.dto.TheatreRequest;
import com.atquil.seatsagaplatform.dto.TheatreResponse;
import com.atquil.seatsagaplatform.entity.*;
import com.atquil.seatsagaplatform.repo.MovieRepository;
import com.atquil.seatsagaplatform.repo.PartnerRepository;
import com.atquil.seatsagaplatform.repo.ShowRepository;
import com.atquil.seatsagaplatform.repo.TheatreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author atquil
 */
@Service
@RequiredArgsConstructor
public class TheatreService {

    private final TheatreRepository theatreRepository;
    private final PartnerRepository partnerRepository;
    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;

    @Transactional(readOnly = true)
    public List<TheatreResponse> getByPartner(Long partnerId) {
        List<Theatre> theatres = theatreRepository.findByPartnerId(partnerId);
        return theatres.stream()
                .map(t -> new TheatreResponse(
                        t.getId(),
                        t.getName(),
                        t.getCity(),
                        t.getAddress(),
                        t.getPartner().getId()
                ))
                .toList();
    }

    @Transactional
    public Theatre add(Long partnerId, TheatreRequest request) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new EntityNotFoundException("Partner not found"));

        Theatre theatre = Theatre.builder()
                .partner(partner)
                .name(request.name())
                .city(request.city())
                .address(request.address())
                .build();
        return theatreRepository.save(theatre);
    }



    // New method: Get all shows for a particular theater (useful for admin UI)
    @Transactional(readOnly = true)
    public List<Show> getByTheatre(Long theatreId) {
        return showRepository.findByScreenTheatreId(theatreId);
    }

    @Transactional(readOnly = true)
    public Theatre getTheatre(Long partnerId, Long theatreId) {
        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(() -> new EntityNotFoundException("Theatre not found"));

        if (!theatre.getPartner().getId().equals(partnerId)) {
            throw new IllegalArgumentException("Theatre does not belong to this partner");
        }

        return theatre;
    }

    @Transactional
    public Theatre update(Long partnerId, Long id, TheatreRequest request) {
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Theatre not found: " + id));

        if (!theatre.getPartner().getId().equals(partnerId)) {
            throw new IllegalArgumentException("Theatre does not belong to this partner");
        }

        theatre.setName(request.name());
        theatre.setCity(request.city());
        theatre.setAddress(request.address());

        return theatreRepository.save(theatre);
    }

    public List<Theatre> getAll() {
        return theatreRepository.findAll();
    }

    //For Booking of tickets
    public List<String> findDistinctCities() {
        return theatreRepository.findDistinctCities();
    }
}
