package com.atquil.seatsagaplatform.controller;

import com.atquil.seatsagaplatform.constants.SeatStatus;
import com.atquil.seatsagaplatform.repo.BookingRepository;
import com.atquil.seatsagaplatform.repo.ShowSeatRepository;
import com.atquil.seatsagaplatform.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author atquil
 */
@Component
@Endpoint(id = "seat-saga-stats")
@RequiredArgsConstructor
public class CustomActuatorController {

    private final ShowSeatRepository showSeatRepository;
    private final UserRepository appUserRepository;
    private final BookingRepository bookingRepository;

    @ReadOperation
    public Map<String, Object> getPlatformStats() {
        Map<String, Object> stats = new HashMap<>();

        // 1. System Stats
        stats.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        stats.put("platform", "Seat Saga Platform");
        stats.put("version", "1.0.0");

        // 2. Database Metrics
        long totalSeats = showSeatRepository.count();
        long bookedSeats = showSeatRepository.countByStatus(SeatStatus.BOOKED);
        long availableSeats = showSeatRepository.countByStatus(SeatStatus.AVAILABLE);
        long lockedSeats = showSeatRepository.countByStatus(SeatStatus.LOCKED);

        stats.put("total_seats", totalSeats);
        stats.put("booked_seats", bookedSeats);
        stats.put("available_seats", availableSeats);
        stats.put("locked_seats", lockedSeats);

        // 3. User Metrics
        long totalUsers = appUserRepository.count();
        long totalBookings = bookingRepository.count();
        stats.put("total_users", totalUsers);
        stats.put("total_bookings", totalBookings);

        // 4. Current User Info (if authenticated)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof OAuth2User) {

            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> userInfo = new HashMap<>();

            userInfo.put("name", oauth2User.getAttribute("name"));
            userInfo.put("email", oauth2User.getAttribute("email"));
            userInfo.put("picture", oauth2User.getAttribute("picture"));
            userInfo.put("authenticated", true);

            stats.put("current_user", userInfo);
        } else {
            stats.put("current_user", Map.of("authenticated", false));
        }

        // 5. Performance Metrics (calculated)
        if (totalSeats > 0) {
            double occupancyRate = (double) bookedSeats / totalSeats * 100;
            stats.put("occupancy_rate", String.format("%.2f%%", occupancyRate));
        } else {
            stats.put("occupancy_rate", "0%");
        }

        // 6. Recent Activity (last 24 hours bookings)
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        long recentBookings = bookingRepository.countByCreatedAtAfter(yesterday);
        stats.put("recent_bookings_24h", recentBookings);

        stats.put("system_status", "OPERATIONAL");

        return stats;
    }
}