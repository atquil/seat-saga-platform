package com.atquil.seatsagaplatform.dto;

/**
 * @author atquil
 */
public record MovieResponse(
        Long id,
        String title,
        String description,
        Integer durationMinutes,
        String genre
) {}