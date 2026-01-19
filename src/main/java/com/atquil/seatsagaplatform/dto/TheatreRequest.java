package com.atquil.seatsagaplatform.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * @author atquil
 */
public record TheatreRequest(
        @NotBlank String name,
        @NotBlank String city,
        String address
) {}
