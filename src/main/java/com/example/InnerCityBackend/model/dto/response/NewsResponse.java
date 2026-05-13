package com.example.InnerCityBackend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {
    private String id;
    private String title;
    private String content;

    @JsonProperty("image_url")   // ✅ JSON output stays "image_url"
    private String imageUrl;     // ✅ field renamed to camelCase — fixes the builder error

    @JsonProperty("category_id")
    private String categoryId;

    private String continent;
    private String country;

    @JsonProperty("isGlobal")
    private Boolean isGlobal;    // ✅ Boolean not boolean

    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}