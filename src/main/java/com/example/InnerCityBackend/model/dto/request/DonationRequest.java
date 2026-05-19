package com.example.InnerCityBackend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DonationRequest {
    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Full name is required")
    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("kings_chat_full_name")
    private String kingsChatFullName;
    private String continent;

    @JsonProperty("subcategory_id")
    private String subcategoryId;
}