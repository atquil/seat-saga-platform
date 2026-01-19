package com.atquil.seatsagaplatform.dto;

/**
 * @author atquil
 */
public record TheatreResponse(
        Long id,
        String name,
        String city,
        String address,
        Long partnerId
) {}