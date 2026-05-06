package com.example.InnerCityBackend.controller;

import com.example.InnerCityBackend.model.dto.request.UpdateProfileRequest;
import com.example.InnerCityBackend.model.dto.response.UserResponse;
import com.example.InnerCityBackend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "User profile edit ")

public class UserController {

    private final UserService userService;


    @PatchMapping(value = "/profile", consumes = {"multipart/form-data"})
    public ResponseEntity<UserResponse> updateProfile(
            Principal principal,
            @RequestPart("data") UpdateProfileRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {


        String userEmail = principal.getName();

        return ResponseEntity.ok(userService.updateProfile(userEmail, request, image));
    }


    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Principal principal) {
        return ResponseEntity.ok(userService.getUserByEmail(principal.getName()));
    }
}