package com.example.InnerCityBackend.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCountRequest {

    @NotNull(message = "Count is required")
    @Min(value = 0, message = "Count cannot be negative")
    private Integer count;
}