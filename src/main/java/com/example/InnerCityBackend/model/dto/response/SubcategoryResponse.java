package com.example.InnerCityBackend.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubcategoryResponse {
    private String id;
    private String name;
    private String description;
    private String categoryId;
    private CategoryResponse category;
}