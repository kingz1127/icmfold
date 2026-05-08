package com.example.InnerCityBackend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UpdateOutreachRequest {
    private String title;
    private String description;

    @JsonProperty("full_address")
    private String fullAddress;

    private String continent;
    private String country;
    private String state;
    private String city;
    private String status; // UPCOMING, ONGOING, etc.

    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonProperty("end_date")
    private LocalDateTime endDate;
}