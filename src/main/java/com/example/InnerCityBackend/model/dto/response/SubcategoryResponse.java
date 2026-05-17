package com.example.InnerCityBackend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubcategoryResponse {
    private String id;
    @JsonProperty("category_id")
    private String categoryId;  // Only the ID, not the whole Category object
    private String name;
    private String description;

    private CategoryResponse category;
}