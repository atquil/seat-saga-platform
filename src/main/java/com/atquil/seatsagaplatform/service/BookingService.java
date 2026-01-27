package com.atquil.seatsagaplatform.service;

import com.atquil.seatsagaplatform.constants.BookingStatus;
import com.atquil.seatsagaplatform.constants.SeatStatus;
import com.atquil.seatsagaplatform.dto.BookingRequest;
import com.atquil.seatsagaplatform.dto.BookingResponse;
import com.atquil.seatsagaplatform.entity.AppUser;
import com.atquil.seatsagaplatform.entity.Booking;
import com.atquil.seatsagaplatform.entity.ShowSeat;
import com.atquil.seatsagaplatform.exception.SeatNotAvailableException;
import com.atquil.seatsagaplatform.repo.BookingRepository;
import com.atquil.seatsagaplatform.repo.ShowSeatRepository;
import com.atquil.seatsagaplatform.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author atquil
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final ShowSeatRepository showSeatRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingResponse createBooking(BookingRequest request) {
        // 1. Identify the User (from Security Context)
        String userEmail = getAuthenticatedUserEmail();
        AppUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userEmail));

        // 2. Parse seat identifiers (e.g., "A-5") to get actual seat IDs
        List<Long> seatDatabaseIds = parseSeatIdentifiers(request.seatIds(), request.showId());

        // 3. Fetch Requested Seats
        List<ShowSeat> selectedSeats = showSeatRepository.findSeatsForBooking(request.showId(), seatDatabaseIds);

        if (selectedSeats.size() != request.seatIds().size()) {
            throw new IllegalArgumentException("Invalid seat IDs provided for this show.");
        }

        // 4. Validation: Are they all AVAILABLE?
        for (ShowSeat seat : selectedSeats) {
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new SeatNotAvailableException("Seat " + seat.getSeat().getRowNumber() +
                        seat.getSeat().getSeatNumber() + " is already booked or locked.");
            }
        }

        try {
            // 5. Calculate Total (use actual seat prices, not the request total)
            BigDecimal totalAmount = selectedSeats.stream()
                    .map(ShowSeat::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Optional: Validate total matches request total (with tolerance)
            BigDecimal requestedTotal = request.totalAmount();
            BigDecimal difference = totalAmount.subtract(requestedTotal).abs();
            if (difference.compareTo(new BigDecimal("0.01")) > 0) { // Allow 1 cent difference
                log.warn("Price mismatch: Calculated={}, Requested={}", totalAmount, requestedTotal);
            }

            // 6. Create Booking Entity (PENDING)
            Booking booking = Booking.builder()
                    .user(user)
                    .show(selectedSeats.getFirst().getShow()) // All seats belong to same show
                    .bookingReference(UUID.randomUUID().toString())
                    .status(BookingStatus.PENDING) // Pending Payment
                    .totalAmount(totalAmount) // Use calculated total
                    .build();

            Booking savedBooking = bookingRepository.save(booking);

            // 7. Lock Seats & Link to Booking
            for (ShowSeat seat : selectedSeats) {
                seat.setStatus(SeatStatus.LOCKED);
                seat.setBooking(savedBooking);
            }
            showSeatRepository.saveAll(selectedSeats);

            // 8. Map to Response DTO
            return mapToResponse(savedBooking, selectedSeats);

        } catch (ObjectOptimisticLockingFailureException e) {
            // This catches the race condition if someone else booked milliseconds ago
            throw new SeatNotAvailableException("One or more seats were just booked by another user. Please retry.");
        }
    }

    private List<Long> parseSeatIdentifiers(List<String> seatIdentifiers, Long showId) {
        List<Long> databaseIds = new ArrayList<>();

        for (String identifier : seatIdentifiers) {
            try {
                // Try to parse as direct database ID first
                Long directId = Long.parseLong(identifier);

                // Verify this ID exists for this show
                ShowSeat showSeat = showSeatRepository.findById(directId)
                        .orElseThrow(() -> new IllegalArgumentException("Seat ID not found: " + directId));

                if (!showSeat.getShow().getId().equals(showId)) {
                    throw new IllegalArgumentException("Seat " + directId + " doesn't belong to show " + showId);
                }

                databaseIds.add(showSeat.getSeat().getId()); // Use seat.id, not show_seat.id

            } catch (NumberFormatException e) {
                // If not a number, try parsing as "A-5" format
                String[] parts = identifier.split("-");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid seat format: " + identifier +
                            ". Expected format: 'ROW-SEATNUMBER' (e.g., 'A-5') or numeric ID");
                }

                try {
                    String row = parts[0];
                    int seatNumber = Integer.parseInt(parts[1]);

                    // Find the actual seat in database
                    ShowSeat showSeat = showSeatRepository.findByShowIdAndRowAndSeatNumber(showId, row, seatNumber);

                    if (showSeat == null) {
                        throw new IllegalArgumentException("Seat not found: " + identifier +
                                " for show ID: " + showId);
                    }

                    databaseIds.add(showSeat.getSeat().getId()); // Use seat.id, not show_seat.id

                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Invalid seat number in: " + identifier, ex);
                }
            }
        }

        return databaseIds;
    }

    private String getAuthenticatedUserEmail() {
        // Extract email from Google OAuth2 Principal
        var principal = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        if (principal instanceof OAuth2User oauthUser) {
            return oauthUser.getAttribute("email");
        }
        throw new IllegalStateException("User not authenticated correctly");
    }

    private BookingResponse mapToResponse(Booking booking, List<ShowSeat> seats) {
        List<String> seatLabels = seats.stream()
                .map(s -> s.getSeat().getRowNumber() + s.getSeat().getSeatNumber())
                .collect(Collectors.toList());

        return new BookingResponse(
                booking.getBookingReference(),
                booking.getShow().getId(),
                booking.getShow().getMovie().getTitle(),
                booking.getShow().getScreen().getTheatre().getName(),
                booking.getShow().getStartTime(),
                seatLabels,
                booking.getTotalAmount(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}