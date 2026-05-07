package com.example.InnerCityBackend.controller;

import com.example.InnerCityBackend.model.dto.request.PartnerRequest;
import com.example.InnerCityBackend.model.dto.response.PartnerResponse;
import com.example.InnerCityBackend.model.dto.response.SuccessResponse;
import com.example.InnerCityBackend.service.PartnerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/partners")
@RequiredArgsConstructor
@Tag(name = "Partner Controller", description = "Admin create and upload partner picture or logo")
public class PartnerController {

    private final PartnerService partnerService;

    // PUBLIC: User and Guest can view
    @GetMapping
    public ResponseEntity<List<PartnerResponse>> getAll() {
        return ResponseEntity.ok(partnerService.getAll());
    }

    // ADMIN ONLY: Create
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PartnerResponse> create(
            @RequestPart("data") PartnerRequest request,
            @RequestPart("image") MultipartFile file) {
        return ResponseEntity.ok(partnerService.createPartner(request, file));
    }

    // ADMIN ONLY: Update
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PartnerResponse> update(
            @PathVariable String id,
            @RequestPart("data") PartnerRequest request,
            @RequestPart(value = "image", required = false) MultipartFile file) {
        return ResponseEntity.ok(partnerService.updatePartner(id, request, file));
    }

    // ADMIN ONLY: Delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> delete(@PathVariable String id) {
        partnerService.delete(id);
        return ResponseEntity.ok(new SuccessResponse("Partner removed"));
    }
}