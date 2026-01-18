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

        // 2. Fetch Requested Seats
        List<ShowSeat> selectedSeats = showSeatRepository.findSeatsForBooking(request.showId(), request.seatIds());

        if (selectedSeats.size() != request.seatIds().size()) {
            throw new IllegalArgumentException("Invalid seat IDs provided for this show.");
        }

        // 3. Validation: Are they all AVAILABLE?
        // Synchronized check before we attempt to write
        for (ShowSeat seat : selectedSeats) {
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new SeatNotAvailableException("Seat " + seat.getSeat().getRowNumber() +
                        seat.getSeat().getSeatNumber() + " is already booked or locked.");
            }
        }

        try {
            // 4. Calculate Total
            BigDecimal totalAmount = selectedSeats.stream()
                    .map(ShowSeat::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 5. Create Booking Entity (PENDING)
            Booking booking = Booking.builder()
                    .user(user)
                    .show(selectedSeats.getFirst().getShow()) // All seats belong to same show
                    .bookingReference(UUID.randomUUID().toString())
                    .status(BookingStatus.PENDING) // Pending Payment
                    .totalAmount(totalAmount)
                    .build();

            Booking savedBooking = bookingRepository.save(booking);

            // 6. Lock Seats & Link to Booking
            //This is where the magic happens. We handle the Optimistic Locking here. If two users try to book the same seat at the exact same millisecond, one will succeed, and the other will get an ObjectOptimisticLockingFailureException, which we catch and convert to a friendly error.
            // This is where @Version check happens automatically upon transaction commit
            for (ShowSeat seat : selectedSeats) {
                seat.setStatus(SeatStatus.LOCKED);
                seat.setBooking(savedBooking);
            }
            showSeatRepository.saveAll(selectedSeats);

            // 7. Map to Response DTO
            return mapToResponse(savedBooking, selectedSeats);

        } catch (ObjectOptimisticLockingFailureException e) {
            // This catches the race condition if someone else booked milliseconds ago
            throw new SeatNotAvailableException("One or more seats were just booked by another user. Please retry.");
        }
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
