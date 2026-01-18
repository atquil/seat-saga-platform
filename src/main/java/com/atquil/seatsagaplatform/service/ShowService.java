package com.atquil.seatsagaplatform.service;

import com.atquil.seatsagaplatform.dto.SeatDTO;
import com.atquil.seatsagaplatform.dto.ShowDetailResponse;
import com.atquil.seatsagaplatform.dto.ShowSearchResponse;
import com.atquil.seatsagaplatform.entity.Show;
import com.atquil.seatsagaplatform.entity.ShowSeat;
import com.atquil.seatsagaplatform.repo.ShowRepository;
import com.atquil.seatsagaplatform.repo.ShowSeatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author atquil
 */
@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;

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
                    var firstShow = theatreShows.get(0);
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
                        ss.getStatus()
                ))
                .toList();
    }
}
