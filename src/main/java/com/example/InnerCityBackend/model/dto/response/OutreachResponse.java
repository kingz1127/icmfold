package com.example.InnerCityBackend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("full_address")
    private String fullAddress;
    private String continent;
    private String country;
    private String state;
    private String city;

    @JsonProperty("subcategory_id")
    private String subcategoryId;

    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonProperty("end_date")
    private LocalDateTime endDate;

    private String status;        // Enum name
    @JsonProperty("approval_status")
    private String approvalStatus;   // Enum name

    @JsonProperty("beneficiaries_count")
    private Integer beneficiariesCount;

    @JsonProperty("volunteers_count")
    private Integer volunteersCount;



    // Nested Hierarchy: Outreach -> Subcategory -> Category
    private SubcategoryResponse subcategory;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    private Double latitude;
    private Double longitude;


}
