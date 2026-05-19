package com.example.InnerCityBackend.controller;

import com.example.InnerCityBackend.model.dto.request.VolunteerRequest;
import com.example.InnerCityBackend.model.dto.response.VolunteerResponse;
import com.example.InnerCityBackend.model.dto.response.SuccessResponse;
import com.example.InnerCityBackend.model.enums.ApprovalStatus;
import com.example.InnerCityBackend.service.VolunteerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("volunteers")
@RequiredArgsConstructor
public class VolunteerController {

    private final VolunteerService volunteerService;

    // PUBLIC: Anyone can sign up to volunteer
    @PostMapping("/signup")
    @Operation(summary = "Anyone can sign up to volunteer")
    public ResponseEntity<VolunteerResponse> signUp(@Valid @RequestBody VolunteerRequest request) {
        return ResponseEntity.ok(volunteerService.signUp(request));
    }

    // ADMIN: Search and filter through volunteer applications
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<VolunteerResponse>> search(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(volunteerService.searchVolunteers(query, PageRequest.of(page, 20)));
    }

    // ADMIN: Approve or Reject a volunteer
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve or Reject a volunteer")
    public ResponseEntity<SuccessResponse> updateStatus(
            @PathVariable String id,
            @RequestParam ApprovalStatus status) {
        volunteerService.updateStatus(id, status);
        return ResponseEntity.ok(new SuccessResponse("Volunteer status updated to " + status));
    }
}