package com.example.InnerCityBackend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateOutreachRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Subcategory ID is required")
    @JsonProperty("subcategory_id") // Matches the snake_case from React Native
    private String subcategoryId;

    @NotBlank(message = "Full address is required")
    @JsonProperty("full_address")
    private String fullAddress;

    private String continent;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "City is required")
    private String city;

    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonProperty("end_date")
    private LocalDateTime endDate;

    // These fields allow the frontend to send coordinates if they used "Pick my location"
    // If they are null, the Service will use the GeoCodingService to find them.
    private Double latitude;
    private Double longitude;
}