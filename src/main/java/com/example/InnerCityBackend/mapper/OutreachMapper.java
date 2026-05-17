

package com.example.InnerCityBackend.mapper;

import com.example.InnerCityBackend.model.dto.response.CategoryResponse;
import com.example.InnerCityBackend.model.dto.response.OutreachResponse;
import com.example.InnerCityBackend.model.dto.response.SubcategoryResponse;
import com.example.InnerCityBackend.model.entity.Outreach;
import org.springframework.stereotype.Component;

@Component
public class OutreachMapper {

    public OutreachResponse toResponse(Outreach o) {
        // Handle null safety
        if (o == null) {
            return null;
        }

        // Build Subcategory Response with its Category info
        SubcategoryResponse subDto = null;
        if (o.getSubcategory() != null) {
            // Build Category DTO (nested inside SubcategoryResponse)
            CategoryResponse catDto = null;
            if (o.getSubcategory().getCategory() != null) {
                catDto = CategoryResponse.builder()
                        .id(o.getSubcategory().getCategory().getId())
                        .name(o.getSubcategory().getCategory().getName())
                        .description(o.getSubcategory().getCategory().getDescription())
                        .build();
            }

            // Build Subcategory DTO
            subDto = SubcategoryResponse.builder()
                    .id(o.getSubcategory().getId())
                    .name(o.getSubcategory().getName())
                    .description(o.getSubcategory().getDescription())
                    .categoryId(o.getSubcategory().getCategory() != null ?
                            o.getSubcategory().getCategory().getId() : null)
                    .category(catDto)
                    .build();
        }

        // Build Final Outreach Response
        return OutreachResponse.builder()
                .id(o.getId())
                .title(o.getTitle())
                .description(o.getDescription())
                .fullAddress(o.getFullAddress())
                .continent(o.getContinent())
                .country(o.getCountry())
                .state(o.getState())
                .city(o.getCity())
                .longitude(o.getLongitude())
                .latitude(o.getLatitude())
                .startDate(o.getStartDate())
                .endDate(o.getEndDate())
                .status(o.getStatus() != null ? o.getStatus().name() : null)
                .approvalStatus(o.getApprovalStatus() != null ? o.getApprovalStatus().name() : null)
                .beneficiariesCount(o.getBeneficiariesCount())
                .volunteersCount(o.getVolunteersCount())
                .createdBy(o.getCreatedBy())
                .subcategory(subDto)
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .subcategoryId(o.getSubcategory() != null ? o.getSubcategory().getId() : null)
                .build();
    }
}