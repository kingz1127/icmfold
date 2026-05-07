package com.example.InnerCityBackend.model.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class PartnerResponse {
    private String id;
    private String partnerName;
    private String partnerImage;
    private LocalDateTime createdAt;
}