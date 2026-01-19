package com.atquil.seatsagaplatform.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author atquil
 */
public record ShowRequest(
        @NotNull Long movieId,
        @Future LocalDateTime startTime,
        @Future LocalDateTime endTime,
        @Min(0) BigDecimal basePrice
) {}
