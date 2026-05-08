package com.example.InnerCityBackend.service;

import com.example.InnerCityBackend.exception.BusinessException;
import com.example.InnerCityBackend.model.dto.request.*;
import com.example.InnerCityBackend.model.dto.response.*;
import com.example.InnerCityBackend.model.entity.*;
import com.example.InnerCityBackend.model.enums.*;
import com.example.InnerCityBackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutreachService {

    private final OutreachRepository outreachRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final UserRepository userRepository;
    // FIX: Added the missing GeoCodingService injection
    private final GeoCodingService geoCodingService;

    @Transactional
    public OutreachResponse createOutreach(CreateOutreachRequest req, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new BusinessException("User not found"));
        Subcategory sub = subcategoryRepository.findById(req.getSubcategoryId())
                .orElseThrow(() -> new BusinessException("Subcategory not found"));

        // Logic for "Auto-Pinpointing":
        // If the frontend didn't send coordinates (user typed address), we get them here.
        Double lat = req.getLatitude();
        Double lng = req.getLongitude();

        if (lat == null || lng == null) {
            double[] coords = geoCodingService.getCoordinates(req.getFullAddress());
            lat = coords[0];
            lng = coords[1];
        }

        Outreach outreach = Outreach.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .fullAddress(req.getFullAddress())
                .continent(req.getContinent())
                .country(req.getCountry())
                .state(req.getState())
                .city(req.getCity())
                .latitude(lat)  // Saved automatically
                .longitude(lng) // Saved automatically
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .subcategory(sub)
                .createdBy(user.getId())
                .status(OutreachStatus.UPCOMING)
                .approvalStatus(ApprovalStatus.PENDING)
                .build();

        return mapToResponse(outreachRepository.save(outreach));
    }

    @Transactional
    public void uploadBulkCsv(MultipartFile file, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail).orElseThrow(() -> new BusinessException("Admin not found"));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            reader.readLine(); // Skip Header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 4) continue; // Basic validation

                String address = data[3].trim();
                Subcategory sub = subcategoryRepository.findById(data[2].trim()).orElse(null);

                if (sub == null) continue;

                // AUTOMATIC PINPOINTING FOR CSV
                double[] coords = geoCodingService.getCoordinates(address);

                Outreach outreach = Outreach.builder()
                        .title(data[0].trim())
                        .description(data[1].trim())
                        .fullAddress(address)
                        .latitude(coords[0])
                        .longitude(coords[1])
                        .country(data[4].trim())
                        .state(data[5].trim())
                        .city(data[6].trim())
                        .createdBy(admin.getId())
                        .status(OutreachStatus.UPCOMING)
                        .approvalStatus(ApprovalStatus.APPROVED) // Bulk uploads by admin are auto-approved
                        .build();

                outreachRepository.save(outreach);
            }
        } catch (Exception e) {
            throw new BusinessException("CSV Processing failed: " + e.getMessage());
        }
    }

    @Transactional
    public OutreachResponse updateOutreach(String id, UpdateOutreachRequest req) {
        Outreach o = outreachRepository.findById(id).orElseThrow(() -> new BusinessException("Outreach not found"));

        if (req.getTitle() != null) o.setTitle(req.getTitle());
        if (req.getDescription() != null) o.setDescription(req.getDescription());

        // If address changes, we re-geocode the location
        if (req.getFullAddress() != null && !req.getFullAddress().equals(o.getFullAddress())) {
            o.setFullAddress(req.getFullAddress());
            double[] coords = geoCodingService.getCoordinates(req.getFullAddress());
            o.setLatitude(coords[0]);
            o.setLongitude(coords[1]);
        }

        if (req.getContinent() != null) o.setContinent(req.getContinent());
        if (req.getCountry() != null) o.setCountry(req.getCountry());
        if (req.getState() != null) o.setState(req.getState());
        if (req.getCity() != null) o.setCity(req.getCity());
        if (req.getStartDate() != null) o.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) o.setEndDate(req.getEndDate());

        if (req.getStatus() != null) {
            o.setStatus(OutreachStatus.valueOf(req.getStatus().toUpperCase()));
        }

        return mapToResponse(outreachRepository.save(o));
    }

    @Transactional
    public OutreachResponse approveOrReject(String id, ApproveRejectRequest req) {
        Outreach o = outreachRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Outreach not found"));

        o.setApprovalStatus(req.getStatus());
        o.setRejectionReason(req.getReason()); // Ensure this field exists in your Outreach entity

        return mapToResponse(outreachRepository.save(o));
    }

    public List<OutreachResponse> getAll() {
        return outreachRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public OutreachResponse getById(String id) {
        return outreachRepository.findById(id).map(this::mapToResponse)
                .orElseThrow(() -> new BusinessException("Outreach not found"));
    }

    @Transactional
    public void delete(String id) {
        if (!outreachRepository.existsById(id)) {
            throw new BusinessException("Outreach not found");
        }
        outreachRepository.deleteById(id);
    }

    @Transactional
    public void updateBeneficiariesCount(String id, Integer count) {
        Outreach o = outreachRepository.findById(id).orElseThrow(() -> new BusinessException("Not found"));
        o.setBeneficiariesCount(count);
        outreachRepository.save(o);
    }

    private OutreachResponse mapToResponse(Outreach o) {
        CategoryResponse catDto = CategoryResponse.builder()
                .id(o.getSubcategory().getCategory().getId())
                .name(o.getSubcategory().getCategory().getName())
                .build();

        SubcategoryResponse subDto = SubcategoryResponse.builder()
                .id(o.getSubcategory().getId())
                .name(o.getSubcategory().getName())
                .category(catDto)
                .build();

        return OutreachResponse.builder()
                .id(o.getId()).title(o.getTitle()).description(o.getDescription())
                .fullAddress(o.getFullAddress()).continent(o.getContinent())
                .country(o.getCountry()).state(o.getState()).city(o.getCity())
                .latitude(o.getLatitude()).longitude(o.getLongitude())
                .startDate(o.getStartDate()).endDate(o.getEndDate())
                .status(o.getStatus().name())
                .approvalStatus(o.getApprovalStatus().name())
                .beneficiariesCount(o.getBeneficiariesCount())
                .volunteersCount(o.getVolunteersCount())
                .subcategory(subDto)
                .createdBy(o.getCreatedBy())
                .createdAt(o.getCreatedAt())
                .build();
    }
}