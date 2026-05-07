package com.example.InnerCityBackend.model.dto.request;


import lombok.Data;

@Data
public class UpdateSubcategoryRequest {
    String categoryId;
    String name;
    String description;
}