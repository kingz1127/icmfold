package com.example.InnerCityBackend.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryWithSubcategoriesResponse {
    private String id;
    private String name;
    private String description;
    private List<SubcategoryResponse> subcategories;
}
