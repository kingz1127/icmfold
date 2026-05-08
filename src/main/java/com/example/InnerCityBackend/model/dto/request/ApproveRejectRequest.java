package com.example.InnerCityBackend.model.dto.request;

import com.example.InnerCityBackend.model.enums.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApproveRejectRequest {
    @NotNull
    private ApprovalStatus status; // APPROVED or REJECTED

    private String reason; // Optional text explaining why it was rejected
}