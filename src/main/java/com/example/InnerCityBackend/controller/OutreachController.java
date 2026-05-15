//package com.example.InnerCityBackend.controller;
//
//import com.example.InnerCityBackend.model.dto.request.*;
//import com.example.InnerCityBackend.model.dto.response.*;
//import com.example.InnerCityBackend.service.OutreachService;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.security.Principal;
//import java.util.List;
//
//@RestController
//@RequestMapping("outreaches")
//@Tag(name = "Outreach Controller", description = "Management of community events")
//@RequiredArgsConstructor
//public class OutreachController {
//
//    private final OutreachService outreachService;
//
//    // CREATE
//    @PostMapping
//    public ResponseEntity<OutreachResponse> create(@Valid @RequestBody CreateOutreachRequest req, Principal p) {
//        return ResponseEntity.ok(outreachService.createOutreach(req, p.getName()));
//    }
//
//    // UPDATE (Admin Only)
//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<OutreachResponse> update(@PathVariable String id, @RequestBody UpdateOutreachRequest req) {
//        return ResponseEntity.ok(outreachService.updateOutreach(id, req));
//    }
//
//    // APPROVE / REJECT (Admin Only)
//    @PatchMapping("/{id}/approve")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<OutreachResponse> approve(@PathVariable String id, @RequestBody ApproveRejectRequest req) {
//        return ResponseEntity.ok(outreachService.approveOrReject(id, req));
//    }
//
//    // DELETE (Admin Only)
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<SuccessResponse> delete(@PathVariable String id) {
//        outreachService.delete(id);
//        return ResponseEntity.ok(new SuccessResponse("Outreach deleted successfully"));
//    }
//
//    // READ ALL
//    @GetMapping
//    public ResponseEntity<List<OutreachResponse>> getAll() {
//        return ResponseEntity.ok(outreachService.getAll());
//    }
//
//    // READ ONE
//    @GetMapping("/{id}")
//    public ResponseEntity<OutreachResponse> getOne(@PathVariable String id) {
//        return ResponseEntity.ok(outreachService.getById(id));
//    }
//
//    // BULK CSV (Admin Only)
//    @PostMapping(value = "/bulk-upload", consumes = "multipart/form-data")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<SuccessResponse> bulkUpload(@RequestParam("file") MultipartFile file, Principal p) {
//        OutreachService.BulkUploadResult result = outreachService.uploadBulkCsv(file, p.getName());
//        return ResponseEntity.ok(new SuccessResponse(
//                "Bulk upload complete: " + result.saved() + " saved, " + result.skipped() + " skipped."
//        ));
//    }
//}





package com.example.InnerCityBackend.controller;

import com.example.InnerCityBackend.model.dto.request.*;
import com.example.InnerCityBackend.model.dto.response.*;
import com.example.InnerCityBackend.service.OutreachService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("outreaches")
@Tag(name = "Outreach", description = "Outreach activity management")
@RequiredArgsConstructor
public class OutreachController {

    private final OutreachService outreachService;

    @Operation(summary = "Create a new outreach activity")
    @PostMapping
    public ResponseEntity<OutreachResponse> create(
            @Valid @RequestBody CreateOutreachRequest req, Principal p) {
        return ResponseEntity.ok(outreachService.createOutreach(req, p.getName()));
    }

    @Operation(summary = "List all outreach activities")
    @GetMapping
    public ResponseEntity<List<OutreachResponse>> getAll() {
        return ResponseEntity.ok(outreachService.getAll());
    }

    @Operation(summary = "Get outreach activity by ID (includes subcategory and category)")
    @GetMapping("/{id}")
    public ResponseEntity<OutreachResponse> getOne(@PathVariable String id) {
        return ResponseEntity.ok(outreachService.getById(id));
    }

    @Operation(summary = "Update an outreach activity")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OutreachResponse> update(
            @PathVariable String id, @RequestBody UpdateOutreachRequest req) {
        return ResponseEntity.ok(outreachService.updateOutreach(id, req));
    }

    @Operation(summary = "Delete an outreach activity")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> delete(@PathVariable String id) {
        outreachService.delete(id);
        return ResponseEntity.ok(new SuccessResponse("Outreach deleted successfully"));
    }

    @Operation(summary = "Approve an outreach activity (admin only)")
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OutreachResponse> approve(@PathVariable String id) {
        return ResponseEntity.ok(outreachService.approve(id));
    }

    @Operation(summary = "Reject an outreach activity (admin only)")
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OutreachResponse> reject(
            @PathVariable String id, @RequestBody ApproveRejectRequest req) {
        return ResponseEntity.ok(outreachService.reject(id, req));
    }

    @Operation(summary = "Update beneficiaries count for an outreach activity")
    @PatchMapping("/{id}/beneficiaries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OutreachResponse> updateBeneficiaries(
            @PathVariable String id, @Valid @RequestBody UpdateCountRequest req) {
        return ResponseEntity.ok(outreachService.updateBeneficiaries(id, req));
    }

    @Operation(summary = "Update volunteers count for an outreach activity")
    @PatchMapping("/{id}/volunteers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OutreachResponse> updateVolunteers(
            @PathVariable String id, @Valid @RequestBody UpdateCountRequest req) {
        return ResponseEntity.ok(outreachService.updateVolunteers(id, req));
    }

    @Operation(summary = "Get nearby outreach activities")
    @GetMapping("/nearby")
    public ResponseEntity<List<OutreachResponse>> getNearby(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double radiusKm) {
        return ResponseEntity.ok(outreachService.getNearby(latitude, longitude, radiusKm));
    }

    @Operation(summary = "Get outreach activities within a map bounding box")
    @GetMapping("/map")
    public ResponseEntity<List<OutreachResponse>> getMap(
            @RequestParam Double northLat,
            @RequestParam Double southLat,
            @RequestParam Double eastLng,
            @RequestParam Double westLng) {
        return ResponseEntity.ok(outreachService.getWithinBounds(northLat, southLat, eastLng, westLng));
    }

    @Operation(summary = "Bulk upload outreach activities via CSV (admin only)")
    @PostMapping(value = "/bulk-upload", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> bulkUpload(
            @RequestParam("file") MultipartFile file, Principal p) {
        OutreachService.BulkUploadResult result = outreachService.uploadBulkCsv(file, p.getName());
        return ResponseEntity.ok(new SuccessResponse(
                "Bulk upload complete: " + result.saved() + " saved, " + result.skipped() + " skipped."
        ));
    }

    @GetMapping("/search")
    @Operation(summary = "pagination of 10 per page")
    public ResponseEntity<Page<OutreachResponse>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String subCategoryId,
            @RequestParam(required = false) String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(outreachService.searchOutreaches(title, subCategoryId, country, pageable));
    }
}