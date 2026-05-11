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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;

    @Transactional
    public PartnerResponse createPartner(PartnerRequest request, MultipartFile image) {
        Partner partner = Partner.builder()
                .partnerName(request.getPartnerName())
                .build();

        // Handle image upload
        if (image != null && !image.isEmpty()) {
            partner.setPartnerImage(processImage(image));
        }

        return mapToResponse(partnerRepository.save(partner));
    }

    @Transactional
    public PartnerResponse updatePartner(String id, PartnerRequest request, MultipartFile image) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Partner not found"));

        if (request.getPartnerName() != null) {
            partner.setPartnerName(request.getPartnerName());
        }

        // Update image only if a new one is uploaded
        if (image != null && !image.isEmpty()) {
            partner.setPartnerImage(processImage(image));
        }

        return mapToResponse(partnerRepository.save(partner));
    }

    private String processImage(MultipartFile file) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            return "data:" + file.getContentType() + ";base64," + base64Image;
        } catch (IOException e) {
            throw new BusinessException("Failed to upload image file");
        }
    }

    public List<PartnerResponse> getAll() {
        return partnerRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PartnerResponse getById(String id) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Partner not found"));
        return mapToResponse(partner);
    }

    @Transactional
    public void delete(String id) {
        if (!partnerRepository.existsById(id)) {
            throw new BusinessException("Cannot delete: Partner not found");
        }
        partnerRepository.deleteById(id);
    }

    private PartnerResponse mapToResponse(Partner partner) {
        return PartnerResponse.builder()
                .id(partner.getId())
                .partnerName(partner.getPartnerName())
                .partnerImage(partner.getPartnerImage())
                .createdAt(partner.getCreatedAt())
                .updatedAt(partner.getUpdatedAt())
                .build();
    }
}