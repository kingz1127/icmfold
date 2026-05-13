package com.example.InnerCityBackend.model.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubcategoryResponse {
    private String id;
    private String categoryId;  // Only the ID, not the whole Category object
    private String name;
    private String description;
}