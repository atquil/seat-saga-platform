package com.atquil.seatsagaplatform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * @author atquil
 */
public record MovieRequest(
        @NotBlank String title,
        String description,
        @Min(1) Integer durationMinutes,
        String genre
) {}
