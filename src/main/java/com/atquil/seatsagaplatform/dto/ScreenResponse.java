package com.atquil.seatsagaplatform.dto;

/**
 * @author atquil
 */
public record ScreenResponse(
        Long id,
        String name,
        Integer totalSeats,
        String seatLayoutJson,
        Long theatreId
) {}
