package com.atquil.seatsagaplatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * @author atquil
 */
public record PartnerRequest(
        @NotBlank String name,
        @NotBlank @Email String contactEmail
) {}
