package com.atquil.seatsagaplatform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * @author atquil
 */
public record ScreenRequest(
        @NotBlank String name,
        @Min(1) Integer totalSeats,
        String seatLayoutJson  // JSON string
) {}
