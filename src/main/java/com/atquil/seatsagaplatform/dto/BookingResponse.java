package com.atquil.seatsagaplatform.dto;

import com.atquil.seatsagaplatform.constants.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author atquil
 */
public record BookingResponse(
        String bookingReference,
        Long showId,
        String movieTitle,
        String theatreName,
        LocalDateTime showTime,
        List<String> seats, // e.g., ["A1", "A2"]
        BigDecimal totalAmount,
        BookingStatus status,
        LocalDateTime createdAt
) {}
