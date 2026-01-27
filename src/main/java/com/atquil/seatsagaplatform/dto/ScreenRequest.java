package com.atquil.seatsagaplatform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author atquil
 */
@Data
public class ScreenRequest {
    @NotBlank(message = "Screen name is required")
    private String name;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Total seats must be at least 1")
    private Integer totalSeats;

    private String seatLayoutJson;  // Optional field

    // Add getter with default layout
    public String getSeatLayoutJson() {
        if (seatLayoutJson == null || seatLayoutJson.trim().isEmpty()) {
            return """
                {
                  "rows": [
                    {"row": "A", "seatCount": 5, "seatType": "REGULAR"},
                    {"row": "B", "seatCount": 5, "seatType": "PREMIUM"}
                  ]
                }
                """;
        }
        return seatLayoutJson;
    }

}

