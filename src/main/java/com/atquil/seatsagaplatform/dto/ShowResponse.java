package com.atquil.seatsagaplatform.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author atquil
 */
public record ShowResponse(
        Long id,
        Long movieId,
        String movieTitle,  // For convenience
        Long screenId,
        String screenName,  // For convenience
        Long theatreId,     // For convenience
        String theatreName, // For convenience
        LocalDateTime startTime,
        LocalDateTime endTime,
        BigDecimal basePrice
) {}
