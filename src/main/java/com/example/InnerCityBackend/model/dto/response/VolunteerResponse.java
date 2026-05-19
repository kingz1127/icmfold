package com.example.InnerCityBackend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerResponse {
    private String id;

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    @JsonProperty("kings_chat_handle")
    private String kingsChatHandle;

    private String country;

    // Use the DTOs here so the mobile app gets the ID AND the Name (e.g. "Health")
    @JsonProperty("category")
    private CategoryResponse preferredMissionField;

    @JsonProperty("subcategory")
    private SubcategoryResponse preferredActivity;

    private String status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}