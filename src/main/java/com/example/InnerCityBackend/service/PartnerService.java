package com.example.InnerCityBackend.service;

import com.example.InnerCityBackend.exception.BusinessException;
import com.example.InnerCityBackend.model.dto.request.PartnerRequest;
import com.example.InnerCityBackend.model.dto.response.PartnerResponse;
import com.example.InnerCityBackend.model.entity.Partner;
import com.example.InnerCityBackend.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;

    @Transactional
    public PartnerResponse createPartner(PartnerRequest request, MultipartFile file) {
        Partner partner = Partner.builder()
                .partnerName(request.getPartnerName())
                .build();

        if (file != null && !file.isEmpty()) {
            partner.setPartnerImage(convertImage(file));
        }

        return mapToResponse(partnerRepository.save(partner));
    }

    @Transactional
    public PartnerResponse updatePartner(String id, PartnerRequest request, MultipartFile file) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Partner not found"));

        partner.setPartnerName(request.getPartnerName());

        if (file != null && !file.isEmpty()) {
            partner.setPartnerImage(convertImage(file));
        }

        return mapToResponse(partnerRepository.save(partner));
    }

    public List<PartnerResponse> getAll() {
        return partnerRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public void delete(String id) {
        partnerRepository.deleteById(id);
    }

    private String convertImage(MultipartFile file) {
        try {
            return "data:" + file.getContentType() + ";base64," +
                    Base64.getEncoder().encodeToString(file.getBytes());
        } catch (IOException e) {
            throw new BusinessException("Image upload failed");
        }
    }

    private PartnerResponse mapToResponse(Partner p) {
        return PartnerResponse.builder()
                .id(p.getId())
                .partnerName(p.getPartnerName())
                .partnerImage(p.getPartnerImage())
                .createdAt(p.getCreatedAt())
                .build();
    }
}