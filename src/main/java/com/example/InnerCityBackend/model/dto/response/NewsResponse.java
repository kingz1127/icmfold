package com.example.InnerCityBackend.model.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {
    private String id;
    private String title;
    private String content;
    private String image_url;
    private String categoryId;
    private String continent;
    private String country;
    private boolean isGlobal;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}