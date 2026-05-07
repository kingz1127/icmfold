package com.example.InnerCityBackend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PartnerRequest {
    @NotBlank(message = "Partner name is required")
    private String partnerName;
}