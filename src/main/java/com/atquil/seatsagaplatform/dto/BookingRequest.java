package com.atquil.seatsagaplatform.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author atquil
 */
public record BookingRequest(
        @NotNull(message = "Show ID is required")
        Long showId,

        @NotNull(message = "At least one seat must be selected")
        @Size(min = 1, message = "You must select at least one seat")
        List<String> seatIds,  // Changed from List<Long> to List<String>

        @NotNull(message = "Total amount is required")
        BigDecimal totalAmount
) {}