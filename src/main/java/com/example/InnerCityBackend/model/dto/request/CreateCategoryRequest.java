package com.example.InnerCityBackend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CreateCategoryRequest {
    @NotBlank String name;
    String description;
}