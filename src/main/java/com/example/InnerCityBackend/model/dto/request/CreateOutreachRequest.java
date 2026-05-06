package com.example.InnerCityBackend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateOutreachRequest {
    @NotBlank
    private String title;
    private String description;
    @NotBlank
    private String subcategoryId;
    @NotBlank
    private String fullAddress;
    private String continent;
    @NotBlank
    private String country;
    @NotBlank
    private String state;
    @NotBlank
    private String city;
    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;
}
