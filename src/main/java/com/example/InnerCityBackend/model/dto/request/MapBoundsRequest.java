package com.example.InnerCityBackend.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MapBoundsRequest {

    @NotNull private Double northLat;
    @NotNull private Double southLat;
    @NotNull private Double eastLng;
    @NotNull private Double westLng;
}