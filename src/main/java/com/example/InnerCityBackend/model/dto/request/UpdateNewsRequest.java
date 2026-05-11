package com.example.InnerCityBackend.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNewsRequest {
    private String title;
    private String content;
    private String categoryId;
    private String continent;
    private String country;
    private Boolean isGlobal;
}