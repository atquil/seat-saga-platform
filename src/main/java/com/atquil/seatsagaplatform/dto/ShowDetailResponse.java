package com.atquil.seatsagaplatform.dto;

import java.time.LocalDateTime;

/**
 * @author atquil
 */
public record ShowDetailResponse(
        Long showId,
        String movieTitle,
        String theatreName,
        String screenName,
        LocalDateTime startTime
) {}
