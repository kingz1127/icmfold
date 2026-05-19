package com.example.InnerCityBackend.service;

import com.example.InnerCityBackend.exception.BusinessException;
import com.example.InnerCityBackend.model.dto.request.VolunteerRequest;
import com.example.InnerCityBackend.model.dto.response.CategoryResponse;
import com.example.InnerCityBackend.model.dto.response.SubcategoryResponse;
import com.example.InnerCityBackend.model.dto.response.VolunteerResponse;
import com.example.InnerCityBackend.model.entity.*;
import com.example.InnerCityBackend.model.enums.ApprovalStatus;
import com.example.InnerCityBackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final CategoryRepository categoryRepository;    // Added
    private final SubcategoryRepository subcategoryRepository; // Added

    @Transactional
    public VolunteerResponse signUp(VolunteerRequest request) {
        // 1. Prevent duplicate applications
        if (volunteerRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new BusinessException("An application with this email already exists.");
        }

        // 2. Fetch the actual Entity objects using the IDs from the request
        // request.getCategoryId() corresponds to the "Mission Field"
        Category missionField = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException("Selected Mission Field not found"));

        // request.getSubcategoryId() corresponds to the "Activity"
        Subcategory activity = subcategoryRepository.findById(request.getSubcategoryId())
                .orElseThrow(() -> new BusinessException("Selected Activity not found"));

        // 3. Build the Entity
        Volunteer volunteer = Volunteer.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase())
                .kingsChatHandle(request.getKingsChatHandle())
                .country(request.getCountry())
                .preferredMissionField(missionField) // Pass the Object, not the ID
                .preferredActivity(activity)         // Pass the Object, not the ID
                .status(ApprovalStatus.PENDING)
                .build();

        return mapToResponse(volunteerRepository.save(volunteer));
    }

    // PAGINATED SEARCH FOR ADMIN
    public Page<VolunteerResponse> searchVolunteers(String query, Pageable pageable) {
        Specification<Volunteer> spec = (root, q, cb) -> {
            if (query == null || query.isEmpty()) return cb.conjunction();
            String pattern = "%" + query.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("fullName")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern),
                    cb.like(cb.lower(root.get("country")), pattern)
            );
        };
        // Use mapToResponse to ensure the Admin sees Names instead of just UUIDs
        return volunteerRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    @Transactional
    public void updateStatus(String id, ApprovalStatus status) {
        Volunteer v = volunteerRepository.findById(id).orElseThrow(() -> new BusinessException("Volunteer not found"));
        v.setStatus(status);
        volunteerRepository.save(v);
    }

    private VolunteerResponse mapToResponse(Volunteer v) {
        // Map Category to its Response DTO
        CategoryResponse catDto = CategoryResponse.builder()
                .id(v.getPreferredMissionField().getId())
                .name(v.getPreferredMissionField().getName())
                .build();

        // Map Subcategory to its Response DTO
        SubcategoryResponse subDto = SubcategoryResponse.builder()
                .id(v.getPreferredActivity().getId())
                .name(v.getPreferredActivity().getName())
                .categoryId(v.getPreferredMissionField().getId())
                .category(catDto) // Nested as requested
                .build();

        return VolunteerResponse.builder()
                .id(v.getId())
                .fullName(v.getFullName())
                .email(v.getEmail())
                .kingsChatHandle(v.getKingsChatHandle())
                .country(v.getCountry())
                .preferredMissionField(catDto) // Return the DTO
                .preferredActivity(subDto)    // Return the DTO
                .status(v.getStatus().name())
                .createdAt(v.getCreatedAt())
                .build();
    }
}