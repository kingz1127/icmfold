package com.example.InnerCityBackend.service;

import com.example.InnerCityBackend.exception.BusinessException;
import com.example.InnerCityBackend.model.dto.request.CreateOutreachRequest;
import com.example.InnerCityBackend.model.entity.Outreach;
import com.example.InnerCityBackend.model.entity.Subcategory;
import com.example.InnerCityBackend.model.enums.ApprovalStatus;
import com.example.InnerCityBackend.model.enums.OutreachStatus;
import com.example.InnerCityBackend.repository.OutreachRepository;
import com.example.InnerCityBackend.repository.SubcategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutreachService {

    private final OutreachRepository outreachRepository;
    private final SubcategoryRepository subcategoryRepository;

    @Transactional
    public Outreach createOutreach(CreateOutreachRequest request) {
        // 1. Find the subcategory linked to this outreach
        Subcategory subcategory = subcategoryRepository.findById(request.getSubcategoryId())
                .orElseThrow(() -> new BusinessException("Subcategory not found"));

        // 2. Build the Outreach Entity
        Outreach outreach = Outreach.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .fullAddress(request.getFullAddress())
                .continent(request.getContinent())
                .country(request.getCountry())
                .state(request.getState())
                .city(request.getCity())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .subcategory(subcategory)
                // Default statuses
                .status(OutreachStatus.UPCOMING)
                .approvalStatus(ApprovalStatus.PENDING)
                .beneficiariesCount(0)
                .volunteersCount(0)
                .build();

        return outreachRepository.save(outreach);
    }

    public List<Outreach> getAllOutreaches() {
        return outreachRepository.findAll();
    }

    public Outreach getOutreachById(String id) {
        return outreachRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Outreach not found with id: " + id));
    }

    @Transactional
    public void updateBeneficiariesCount(String id, Integer count) {
        Outreach outreach = getOutreachById(id);
        outreach.setBeneficiariesCount(count);
        outreachRepository.save(outreach);
    }

    @Transactional
    public void changeApprovalStatus(String id, ApprovalStatus approvalStatus) {
        Outreach outreach = getOutreachById(id);
        outreach.setApprovalStatus(approvalStatus);
        outreachRepository.save(outreach);
    }
}