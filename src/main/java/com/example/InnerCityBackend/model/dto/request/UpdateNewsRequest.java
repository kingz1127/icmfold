package com.example.InnerCityBackend.model.dto.request;

import lombok.Data;

@Data
public class UpdateNewsRequest {
    private String title;
    private String content;
    private String categoryId;
    private String continent;
    private String country;
    private Boolean isGlobal; 
}