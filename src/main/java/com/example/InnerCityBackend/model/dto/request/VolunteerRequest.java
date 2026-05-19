package com.example.InnerCityBackend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VolunteerRequest {
    @NotBlank(message = "Full name is required")
    @JsonProperty("full_name")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @JsonProperty("kings_chat_handle")
    private String kingsChatHandle;

    private String country;

    // The mobile app sends the UUIDs from the dropdowns
    @NotBlank(message = "Mission Field is required")
    @JsonProperty("category_id")
    private String categoryId;

    @NotBlank(message = "Activity is required")
    @JsonProperty("subcategory_id")
    private String subcategoryId;
}