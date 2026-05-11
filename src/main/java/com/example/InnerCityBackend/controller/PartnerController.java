package com.example.InnerCityBackend.controller;

import com.example.InnerCityBackend.model.dto.request.PartnerRequest;
import com.example.InnerCityBackend.model.dto.response.PartnerResponse;
import com.example.InnerCityBackend.model.dto.response.SuccessResponse;
import com.example.InnerCityBackend.service.PartnerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("partners")  // Removed leading slash to match News pattern
@RequiredArgsConstructor
@Tag(name = "Partner Controller", description = "Admin C,R,U,D and Users view")
public class PartnerController {

    private final PartnerService partnerService;

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PartnerResponse> createPartner(
            @RequestParam("partnerName") String partnerName,  // Like News: individual params
            @RequestPart(value = "image", required = false) MultipartFile image) {

        PartnerRequest request = PartnerRequest.builder()
                .partnerName(partnerName)
                .build();

        return ResponseEntity.ok(partnerService.createPartner(request, image));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PartnerResponse> updatePartner(
            @PathVariable String id,
            @RequestParam(value = "partnerName", required = false) String partnerName,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        PartnerRequest request = PartnerRequest.builder()
                .partnerName(partnerName)
                .build();

        return ResponseEntity.ok(partnerService.updatePartner(id, request, image));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> deletePartner(@PathVariable String id) {
        partnerService.delete(id);
        return ResponseEntity.ok(new SuccessResponse("Partner deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<List<PartnerResponse>> getAllPartners() {
        return ResponseEntity.ok(partnerService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartnerResponse> getPartnerById(@PathVariable String id) {
        return ResponseEntity.ok(partnerService.getById(id));
    }
}