package com.atquil.seatsagaplatform.dto;

import com.atquil.seatsagaplatform.constants.SeatStatus;

/**
 * @author atquil
 */
public record SeatDTO(
        Long showSeatId,
        Long seatId,
        String row,
        int number,
        SeatStatus status
) {}