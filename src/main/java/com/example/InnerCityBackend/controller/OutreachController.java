package com.example.InnerCityBackend.controller;

import com.example.InnerCityBackend.model.dto.request.CreateOutreachRequest;
import com.example.InnerCityBackend.model.dto.request.UpdateCountRequest;
import com.example.InnerCityBackend.model.dto.response.SuccessResponse;
import com.example.InnerCityBackend.model.entity.Outreach;
import com.example.InnerCityBackend.model.enums.ApprovalStatus;
import com.example.InnerCityBackend.service.OutreachService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/outreach")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and account management")

public class OutreachController {

    private final OutreachService outreachService;

    @PostMapping
    public ResponseEntity<Outreach> create(@Valid @RequestBody CreateOutreachRequest request) {
        return new ResponseEntity<>(outreachService.createOutreach(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Outreach>> getAll() {
        return ResponseEntity.ok(outreachService.getAllOutreaches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Outreach> getOne(@PathVariable String id) {
        return ResponseEntity.ok(outreachService.getOutreachById(id));
    }


    @PatchMapping("/{id}/beneficiaries")
    public ResponseEntity<SuccessResponse> updateBeneficiaries(
            @PathVariable String id,
            @Valid @RequestBody UpdateCountRequest request) {
        outreachService.updateBeneficiariesCount(id, request.getCount());
        return ResponseEntity.ok(new SuccessResponse("Beneficiaries updated"));
    }


    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> approve(@PathVariable String id) {
        outreachService.changeApprovalStatus(id, ApprovalStatus.APPROVED);
        return ResponseEntity.ok(new SuccessResponse("Outreach Approved"));
    }
}
