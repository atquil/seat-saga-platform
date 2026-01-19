package com.atquil.seatsagaplatform.dto;

import com.atquil.seatsagaplatform.constants.SeatStatus;

import java.math.BigDecimal;

/**
 * @author atquil
 */
public record SeatDTO(
        Long id,
        Long seatId,
        String rowNumber,
        Integer seatNumber,
        SeatStatus status,
        BigDecimal price  // Added price field
) {}