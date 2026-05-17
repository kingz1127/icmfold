package com.example.InnerCityBackend.model.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateSubcategoryRequest {
    @JsonProperty("category_id")
    String categoryId;

    String name;
    String description;
}