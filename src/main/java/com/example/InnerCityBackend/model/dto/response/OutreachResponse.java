package com.example.InnerCityBackend.model.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutreachResponse {
    private String id;
    private String title;
    private String description;
    private String fullAddress;
    private String continent;
    private String country;
    private String state;
    private String city;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String status;           // Enum name
    private String approvalStatus;   // Enum name

    private Integer beneficiariesCount;
    private Integer volunteersCount;

    private String createdBy; // The Admin/User UUID

    // Nested Hierarchy: Outreach -> Subcategory -> Category
    private SubcategoryResponse subcategory;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Double latitude;
    private Double longitude;
}
