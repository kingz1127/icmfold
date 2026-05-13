package com.example.InnerCityBackend.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NearbyOutreachRequest {

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    // Default 10km if not provided
    private Double radiusKm = 10.0;
}