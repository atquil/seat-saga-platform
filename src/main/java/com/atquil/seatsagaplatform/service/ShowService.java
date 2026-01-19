package com.atquil.seatsagaplatform.service;

import com.atquil.seatsagaplatform.constants.SeatStatus;
import com.atquil.seatsagaplatform.constants.SeatType;
import com.atquil.seatsagaplatform.dto.*;
import com.atquil.seatsagaplatform.entity.*;
import com.atquil.seatsagaplatform.repo.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author atquil
 */
@Service
@RequiredArgsConstructor
public class ShowService {

    @Value("${pricing.regular}")
    private BigDecimal regularPrice;

    @Value("${pricing.premium}")
    private BigDecimal premiumPrice;

    @Value("${pricing.recliner}")
    private BigDecimal reclinerPrice;

    @Value("${pricing.vip}")
    private BigDecimal vipPrice;

    @Value("${pricing.accessible}")
    private BigDecimal accessiblePrice;

    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final ScreenRepository screenRepository;
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;


    @Transactional(readOnly = true)
    public List<ShowSearchResponse> searchShows(String city, Long movieId, LocalDate date) {
        // 1. Calculate time range for the query
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // 2. Fetch raw data
        List<Show> shows = showRepository.findShowsByCityAndMovieAndDate(city, movieId, startOfDay, endOfDay);

        // 3. Group by Theatre (Architecture Pattern: Aggregation)
        // We group the flat list of shows by Theatre ID so the UI gets a clean nested structure
        Map<Long, List<Show>> showsByTheatre = shows.stream()
                .collect(Collectors.groupingBy(show -> show.getScreen().getTheatre().getId()));

        // 4. Map to DTO
        return showsByTheatre.values().stream()
                .map(theatreShows -> {
                    var firstShow = theatreShows.getFirst();
                    var theatre = firstShow.getScreen().getTheatre();

                    List<ShowSearchResponse.ShowTimeDTO> showTimes = theatreShows.stream()
                            .map(s -> new ShowSearchResponse.ShowTimeDTO(
                                    s.getId(),
                                    s.getStartTime(),
                                    s.getScreen().getName(),
                                    "English" // Placeholder, in real app add 'language' to Show entity
                            ))
                            .toList();

                    return new ShowSearchResponse(
                            theatre.getId(),
                            theatre.getName(),
                            theatre.getAddress(),
                            showTimes
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)   // ← still good practice
    public ShowDetailResponse getShow(Long id) {
        Show show = showRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Show not found: " + id));

        return new ShowDetailResponse(
                show.getId(),
                show.getMovie().getTitle(),
                show.getScreen().getTheatre().getName(),
                show.getScreen().getName(),
                show.getStartTime()
        );
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
                        ss.getPrice()  // Add price here
                ))
                .toList();
    }



    @Transactional(readOnly = true)
    public List<Show> getByScreen(Long screenId) {
        return showRepository.findByScreenId(screenId);
    }

    @Transactional
    public ShowResponse add(Long screenId, ShowRequest request) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new EntityNotFoundException("Screen not found: " + screenId));

        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new EntityNotFoundException("Movie not found: " + request.movieId()));

        Show show = Show.builder()
                .movie(movie)
                .screen(screen)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .basePrice(request.basePrice())
                .build();

        show = showRepository.save(show);

        // Auto-create ShowSeats with dynamic pricing from environment variables
        List<Seat> seats = screen.getSeats();
        Show finalShow = show;
        List<ShowSeat> showSeats = seats.stream()
                .map(seat -> ShowSeat.builder()
                        .show(finalShow)
                        .seat(seat)
                        .status(SeatStatus.AVAILABLE)
                        .price(calculatePrice(finalShow.getBasePrice(), seat.getSeatType()))
                        .build()
                )
                .toList();

        showSeatRepository.saveAll(showSeats);

        return toResponse(show);
    }

    @Transactional
    public ShowResponse createShow(Long partnerId, Long theatreId, Long screenId, ShowRequest request) {
        // Validate ownership chain
        Theatre theatre = validateTheatreBelongsToPartner(partnerId, theatreId);
        Screen screen = validateScreenBelongsToTheatre(screenId, theatreId);

        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        Show show = Show.builder()
                .movie(movie)
                .screen(screen)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .basePrice(request.basePrice())
                .build();

        show = showRepository.save(show);

        // Auto-create inventory with env-based pricing
        createShowSeatInventory(show);

        return toResponse(show);
    }

    @Transactional
    public ShowResponse updateShow(Long partnerId, Long theatreId, Long showId, ShowRequest request) {
        validateTheatreBelongsToPartner(partnerId, theatreId);
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new EntityNotFoundException("Show not found"));

        if (!show.getScreen().getTheatre().getId().equals(theatreId)) {
            throw new IllegalArgumentException("Show does not belong to this theatre");
        }

        show.setStartTime(request.startTime());
        show.setEndTime(request.endTime());
        show.setBasePrice(request.basePrice());

        if (request.movieId() != null && !show.getMovie().getId().equals(request.movieId())) {
            Movie newMovie = movieRepository.findById(request.movieId())
                    .orElseThrow(() -> new EntityNotFoundException("Movie not found"));
            show.setMovie(newMovie);
        }

        Show updatedShow = showRepository.save(show);
        return toResponse(updatedShow);
    }

    @Transactional
    public void deleteShow(Long partnerId, Long theatreId, Long showId) {
        validateTheatreBelongsToPartner(partnerId, theatreId);
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new EntityNotFoundException("Show not found"));

        if (!show.getScreen().getTheatre().getId().equals(theatreId)) {
            throw new IllegalArgumentException("Show does not belong to this theatre");
        }


        showRepository.delete(show);
    }

    // Seat Inventory
    @Transactional(readOnly = true)
    public List<ShowSeat> getShowSeatInventory(Long showId) {
        return showSeatRepository.findByShowId(showId);
    }

    @Transactional
    public ShowSeat updateShowSeat(Long showId, Long showSeatId, ShowSeatUpdateRequest request) {
        ShowSeat seat = showSeatRepository.findById(showSeatId)
                .orElseThrow(() -> new EntityNotFoundException("ShowSeat not found"));

        if (!seat.getShow().getId().equals(showId)) {
            throw new IllegalArgumentException("Seat does not belong to this show");
        }

        if (seat.getStatus() == SeatStatus.BOOKED) {
            throw new IllegalStateException("Cannot modify booked seat");
        }

        seat.setStatus(request.status());
        seat.setPrice(request.price());

        return showSeatRepository.save(seat);
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> getShowsByTheatre(Long theatreId) {
        List<Show> shows = showRepository.findByScreenTheatreId(theatreId);
        return shows.stream()
                .map(this::toResponse)
                .toList();
    }

    // Helpers
    private Theatre validateTheatreBelongsToPartner(Long partnerId, Long theatreId) {
        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(() -> new EntityNotFoundException("Theatre not found"));
        if (!theatre.getPartner().getId().equals(partnerId)) {
            throw new IllegalArgumentException("Theatre does not belong to this partner");
        }
        return theatre;
    }

    private Screen validateScreenBelongsToTheatre(Long screenId, Long theatreId) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new EntityNotFoundException("Screen not found"));
        if (!screen.getTheatre().getId().equals(theatreId)) {
            throw new IllegalArgumentException("Screen does not belong to this theatre");
        }
        return screen;
    }

    private void createShowSeatInventory(Show show) {
        List<Seat> seats = show.getScreen().getSeats();
        List<ShowSeat> showSeats = seats.stream()
                .map(seat -> ShowSeat.builder()
                        .show(show)
                        .seat(seat)
                        .status(SeatStatus.AVAILABLE)
                        .price(calculatePrice(show.getBasePrice(), seat.getSeatType()))
                        .build())
                .toList();
        showSeatRepository.saveAll(showSeats);
    }

    public BigDecimal calculatePrice(BigDecimal base, SeatType type) {
        return switch (type) {
            case REGULAR -> base.add(regularPrice);
            case PREMIUM -> base.add(premiumPrice);
            case RECLINER -> base.add(reclinerPrice);
            case VIP -> base.add(vipPrice);
            case ACCESSIBLE -> base.add(accessiblePrice);
        };
    }

    @Transactional
    public ShowResponse update(Long id, ShowRequest request) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Show not found: " + id));

        if (request.movieId() != null) {
            Movie movie = movieRepository.findById(request.movieId())
                    .orElseThrow(() -> new EntityNotFoundException("Movie not found"));
            show.setMovie(movie);
        }

        if (request.startTime() != null) show.setStartTime(request.startTime());
        if (request.endTime() != null) show.setEndTime(request.endTime());
        if (request.basePrice() != null) show.setBasePrice(request.basePrice());

        Show updatedShow = showRepository.save(show);
        return toResponse(updatedShow);
    }

    @Transactional
    public void delete(Long id) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Show not found: " + id));
        showRepository.delete(show);
    }

    private ShowResponse toResponse(Show show) {
        return new ShowResponse(
                show.getId(),
                show.getMovie().getId(),
                show.getMovie().getTitle(),
                show.getScreen().getId(),
                show.getScreen().getName(),
                show.getScreen().getTheatre().getId(),
                show.getScreen().getTheatre().getName(),
                show.getStartTime(),
                show.getEndTime(),
                show.getBasePrice()
        );
    }
}
