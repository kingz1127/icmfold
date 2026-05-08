package com.example.InnerCityBackend.model.dto.response;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryWithSubcategoriesResponse {
    private String id;
    private String name;
    private String description;
    private List<SubcategoryResponse> subcategories;
}