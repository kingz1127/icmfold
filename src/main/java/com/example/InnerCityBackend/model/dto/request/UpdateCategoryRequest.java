package com.example.InnerCityBackend.model.dto.request;

import lombok.Data;

@Data
public class UpdateCategoryRequest {
    String name;
    String description;
}