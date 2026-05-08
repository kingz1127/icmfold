package com.example.InnerCityBackend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadResult {
    private int totalRows;
    private int successCount;
    private int failureCount;
    private List<BulkRowError> errors;

    @Data
    @AllArgsConstructor
    public static class BulkRowError {
        private int rowNumber;
        private String title;
        private String reason;
    }
}