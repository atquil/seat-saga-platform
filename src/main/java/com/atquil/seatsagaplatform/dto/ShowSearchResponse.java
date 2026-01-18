package com.atquil.seatsagaplatform.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author atquil
 */
public record ShowSearchResponse(
        Long theatreId,
        String theatreName,
        String address,
        List<ShowTimeDTO> shows
) {
    public record ShowTimeDTO(
            Long showId,
            LocalDateTime startTime,
            String screenName,
            String language
    ) {}
}