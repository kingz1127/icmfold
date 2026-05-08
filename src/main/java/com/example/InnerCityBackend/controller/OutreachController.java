package com.example.InnerCityBackend.controller;

import com.example.InnerCityBackend.model.dto.request.*;
import com.example.InnerCityBackend.model.dto.response.*;
import com.example.InnerCityBackend.service.OutreachService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/outreaches")
@Tag(name = "Outreach Controller", description = "Management of community events")
@RequiredArgsConstructor
public class OutreachController {

    private final OutreachService outreachService;

    // CREATE
    @PostMapping
    public ResponseEntity<OutreachResponse> create(@Valid @RequestBody CreateOutreachRequest req, Principal p) {
        return ResponseEntity.ok(outreachService.createOutreach(req, p.getName()));
    }

    // UPDATE (Admin Only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OutreachResponse> update(@PathVariable String id, @RequestBody UpdateOutreachRequest req) {
        return ResponseEntity.ok(outreachService.updateOutreach(id, req));
    }

    // APPROVE / REJECT (Admin Only)
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OutreachResponse> approve(@PathVariable String id, @RequestBody ApproveRejectRequest req) {
        return ResponseEntity.ok(outreachService.approveOrReject(id, req));
    }

    // DELETE (Admin Only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> delete(@PathVariable String id) {
        outreachService.delete(id);
        return ResponseEntity.ok(new SuccessResponse("Outreach deleted successfully"));
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<OutreachResponse>> getAll() {
        return ResponseEntity.ok(outreachService.getAll());
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<OutreachResponse> getOne(@PathVariable String id) {
        return ResponseEntity.ok(outreachService.getById(id));
    }

    // BULK CSV (Admin Only)
    @PostMapping(value = "/bulk-upload", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> bulkUpload(@RequestParam("file") MultipartFile file, Principal p) {
        outreachService.uploadBulkCsv(file, p.getName());
        return ResponseEntity.ok(new SuccessResponse("Bulk upload processed successfully"));
    }
}