package com.example.InnerCityBackend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSubcategoryRequest {
    @NotBlank String categoryId;
    @NotBlank String name;
    String description;
}