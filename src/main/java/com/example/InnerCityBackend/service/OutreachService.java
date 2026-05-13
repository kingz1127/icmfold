
package com.example.InnerCityBackend.service;

import com.example.InnerCityBackend.exception.BusinessException;
import com.example.InnerCityBackend.mapper.OutreachMapper;
import com.example.InnerCityBackend.model.dto.request.*;
import com.example.InnerCityBackend.model.dto.response.*;
import com.example.InnerCityBackend.model.entity.*;
import com.example.InnerCityBackend.model.enums.*;
import com.example.InnerCityBackend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutreachService {

    private final OutreachRepository outreachRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final UserRepository userRepository;
    private final GeoCodingService geoCodingService;
    private final OutreachMapper outreachMapper; // ✅ injected mapper

    public record BulkUploadResult(int saved, int skipped) {}

    @Transactional
    public OutreachResponse createOutreach(CreateOutreachRequest req, String userEmail) {
        try {
            log.info("Creating outreach for user: {}", userEmail);

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new BusinessException("User not found"));

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
                    .latitude(lat)
                    .longitude(lng)
                    .startDate(req.getStartDate())
                    .endDate(req.getEndDate())
                    .subcategory(sub)
                    .createdBy(user.getId())
                    .status(OutreachStatus.UPCOMING)
                    .approvalStatus(ApprovalStatus.PENDING)
                    .build();

            return outreachMapper.toResponse(outreachRepository.save(outreach));

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating outreach: {}", e.getMessage(), e);
            throw new BusinessException("Failed to create outreach: " + e.getMessage());
        }
    }

    @Transactional
    public OutreachResponse updateOutreach(String id, UpdateOutreachRequest req) {
        try {
            Outreach o = outreachRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("Outreach not found"));

            if (req.getTitle() != null) o.setTitle(req.getTitle());
            if (req.getDescription() != null) o.setDescription(req.getDescription());

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
            if (req.getBeneficiariesCount() != null) o.setBeneficiariesCount(req.getBeneficiariesCount());
            if (req.getVolunteersCount() != null) o.setVolunteersCount(req.getVolunteersCount());
            if (req.getStatus() != null) {
                try {
                    o.setStatus(OutreachStatus.valueOf(req.getStatus().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new BusinessException("Invalid status value: '" + req.getStatus() +
                            "'. Valid values are: " + Arrays.toString(OutreachStatus.values()));
                }
            }

            return outreachMapper.toResponse(outreachRepository.save(o));

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating outreach {}: {}", id, e.getMessage(), e);
            throw new BusinessException("Failed to update outreach: " + e.getMessage());
        }
    }

    // ✅ SPLIT: approve only
    @Transactional
    public OutreachResponse approve(String id) {
        Outreach o = outreachRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Outreach not found"));
        o.setApprovalStatus(ApprovalStatus.APPROVED);
        o.setRejectionReason(null);
        log.info("Outreach {} approved", id);
        return outreachMapper.toResponse(outreachRepository.save(o));
    }

    // ✅ SPLIT: reject with reason
    @Transactional
    public OutreachResponse reject(String id, ApproveRejectRequest req) {
        Outreach o = outreachRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Outreach not found"));
        o.setApprovalStatus(ApprovalStatus.REJECTED);
        o.setRejectionReason(req.getReason());
        log.info("Outreach {} rejected. Reason: {}", id, req.getReason());
        return outreachMapper.toResponse(outreachRepository.save(o));
    }

    // ✅ NEW: update beneficiaries count
    @Transactional
    public OutreachResponse updateBeneficiaries(String id, UpdateCountRequest req) {
        Outreach o = outreachRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Outreach not found"));
        o.setBeneficiariesCount(req.getCount());
        log.info("Outreach {} beneficiaries updated to {}", id, req.getCount());
        return outreachMapper.toResponse(outreachRepository.save(o));
    }

    // ✅ NEW: update volunteers count
    @Transactional
    public OutreachResponse updateVolunteers(String id, UpdateCountRequest req) {
        Outreach o = outreachRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Outreach not found"));
        o.setVolunteersCount(req.getCount());
        log.info("Outreach {} volunteers updated to {}", id, req.getCount());
        return outreachMapper.toResponse(outreachRepository.save(o));
    }

    // ✅ NEW: nearby
    @Transactional(readOnly = true)
    public List<OutreachResponse> getNearby(double lat, double lng, double radiusKm) {
        log.debug("Fetching nearby outreaches: lat={}, lng={}, radius={}km", lat, lng, radiusKm);
        return outreachRepository.findNearby(lat, lng, radiusKm)
                .stream()
                .map(outreachMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ✅ NEW: map bounding box
    @Transactional(readOnly = true)
    public List<OutreachResponse> getWithinBounds(double northLat, double southLat,
                                                  double eastLng, double westLng) {
        log.debug("Fetching outreaches in bounds: N={}, S={}, E={}, W={}", northLat, southLat, eastLng, westLng);
        return outreachRepository.findWithinBoundingBox(southLat, northLat, westLng, eastLng)
                .stream()
                .map(outreachMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OutreachResponse> getAll() {
        return outreachRepository.findAll()
                .stream()
                .map(outreachMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OutreachResponse getById(String id) {
        Outreach outreach = outreachRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Outreach not found"));
        return outreachMapper.toResponse(outreach);
    }

    @Transactional
    public void delete(String id) {
        if (!outreachRepository.existsById(id)) {
            throw new BusinessException("Outreach not found with id: " + id);
        }
        outreachRepository.deleteById(id);
        log.info("Deleted outreach with id: {}", id);
    }

    @Transactional
    public BulkUploadResult uploadBulkCsv(MultipartFile file, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new BusinessException("Admin not found"));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String headerLine = reader.readLine();
            if (headerLine == null) throw new BusinessException("CSV file is empty");

            String line;
            int saved = 0, skipped = 0;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] data = parseCsvLine(line);

                if (data.length < 9) { skipped++; continue; }

                Subcategory sub = subcategoryRepository.findById(data[3].trim()).orElse(null);
                if (sub == null) { skipped++; continue; }

                String fullAddress = data[4].trim();
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
                    lat = coords[0]; lng = coords[1];
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

                outreachRepository.save(Outreach.builder()
                        .title(data[1].trim())
                        .description(data[2].trim())
                        .fullAddress(fullAddress)
                        .continent(data[5].trim())
                        .country(data[6].trim())
                        .state(data[7].trim())
                        .city(data[8].trim())
                        .latitude(lat).longitude(lng)
                        .startDate(startDate).endDate(endDate)
                        .beneficiariesCount(beneficiaries)
                        .volunteersCount(volunteers)
                        .subcategory(sub)
                        .createdBy(admin.getId())
                        .status(OutreachStatus.UPCOMING)
                        .approvalStatus(ApprovalStatus.APPROVED)
                        .build());
                saved++;
            }

            log.info("Bulk upload: {} saved, {} skipped", saved, skipped);
            return new BulkUploadResult(saved, skipped);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("CSV processing failed: {}", e.getMessage(), e);
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
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"'); i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString()); sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }
}