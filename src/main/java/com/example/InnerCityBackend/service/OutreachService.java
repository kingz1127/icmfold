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
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public record BulkUploadResult(int saved, int skipped) {}

    @Transactional
    public OutreachResponse createOutreach(CreateOutreachRequest req, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new BusinessException("User not found"));
        Subcategory sub = subcategoryRepository.findById(req.getSubcategoryId())
                .orElseThrow(() -> new BusinessException("Subcategory not found"));


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


    // 2. Change method signature from void → BulkUploadResult
    @Transactional
    public BulkUploadResult uploadBulkCsv(MultipartFile file, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new BusinessException("Admin not found"));

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            String headerLine = reader.readLine();
            if (headerLine == null) throw new BusinessException("CSV file is empty");

            String line;
            int saved = 0;
            int skipped = 0;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] data = parseCsvLine(line);

                if (data.length < 9) { skipped++; continue; }

                String subcategoryId = data[3].trim();
                Subcategory sub = subcategoryRepository.findById(subcategoryId).orElse(null);
                if (sub == null) { skipped++; continue; }

                String fullAddress = data[4].trim();
                String continent   = data[5].trim();
                String country     = data[6].trim();
                String state       = data[7].trim();
                String city        = data[8].trim();

                Double lat = null, lng = null;
                if (data.length > 9 && data[9].toUpperCase().startsWith("POINT")) {
                    try {
                        String point = data[9].trim()
                                .replace("POINT (", "").replace("POINT(", "").replace(")", "");
                        String[] coords = point.split(" ");
                        lng = Double.parseDouble(coords[0]);
                        lat = Double.parseDouble(coords[1]);
                    } catch (Exception ignored) {}
                }
                if (lat == null || lng == null) {
                    double[] coords = geoCodingService.getCoordinates(fullAddress);
                    lat = coords[0];
                    lng = coords[1];
                }

                LocalDateTime startDate = null, endDate = null;
                if (data.length > 10 && !data[10].isBlank()) {
                    try { startDate = LocalDateTime.parse(data[10].trim()); } catch (Exception ignored) {}
                }
                if (data.length > 11 && !data[11].isBlank()) {
                    try { endDate = LocalDateTime.parse(data[11].trim()); } catch (Exception ignored) {}
                }

                int beneficiaries = 0, volunteers = 0;
                if (data.length > 13 && !data[13].isBlank()) {
                    try { beneficiaries = Integer.parseInt(data[13].trim()); } catch (Exception ignored) {}
                }
                if (data.length > 14 && !data[14].isBlank()) {
                    try { volunteers = Integer.parseInt(data[14].trim()); } catch (Exception ignored) {}
                }

                Outreach outreach = Outreach.builder()
                        .title(data[1].trim())
                        .description(data[2].trim())
                        .fullAddress(fullAddress)
                        .continent(continent)
                        .country(country)
                        .state(state)
                        .city(city)
                        .latitude(lat)
                        .longitude(lng)
                        .startDate(startDate)
                        .endDate(endDate)
                        .beneficiariesCount(beneficiaries)
                        .volunteersCount(volunteers)
                        .subcategory(sub)
                        .createdBy(admin.getId())
                        .status(OutreachStatus.UPCOMING)
                        .approvalStatus(ApprovalStatus.APPROVED)
                        .build();

                outreachRepository.save(outreach);
                saved++;
            }

            // 3. Return result instead of printing
            return new BulkUploadResult(saved, skipped);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("CSV Processing failed: " + e.getMessage());
        }
    }

    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                // Handle escaped quote ""
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString()); // last field
        return fields.toArray(new String[0]);
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