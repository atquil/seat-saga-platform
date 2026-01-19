package com.atquil.seatsagaplatform.dto;

import com.atquil.seatsagaplatform.constants.SeatStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * @author atquil
 */
public record ShowSeatUpdateRequest(
        @NotNull SeatStatus status,
        @NotNull BigDecimal price
) {}
