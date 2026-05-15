package com.example.InnerCityBackend.controller;

import com.example.InnerCityBackend.model.dto.response.UserResponse;
import com.example.InnerCityBackend.model.enums.UserRole;
import com.example.InnerCityBackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("super-admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')") // LOCKED: Only Super Admin can enter
@Tag(name = "Super Admin Controller", description = "User management and role promotion to Admin or demote back to user")
public class SuperAdminController {

    private final UserService userService;

    // Search through all Users and Admins
    @GetMapping("/users/search") @Operation(description = "pagination of 10 per page")
    public ResponseEntity<Page<UserResponse>> searchAllUsers(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.adminSearchUsers(query, PageRequest.of(page, size)));
    }

    // Promote a user to ADMIN or Demote back to USER
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable String userId,
            @RequestParam UserRole role) {

        if (role == UserRole.SUPER_ADMIN) {
            throw new IllegalArgumentException("Cannot create more Super Admins via this endpoint");
        }

        return ResponseEntity.ok(userService.changeUserRole(userId, role));
    }
}