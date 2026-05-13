package com.example.InnerCityBackend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSubcategoryRequest {

    @JsonProperty("category_id")
    @NotBlank String categoryId;
    @NotBlank String name;
    String description;
}