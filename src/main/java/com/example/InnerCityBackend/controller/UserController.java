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
@RequestMapping("users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "User profile management")
public class UserController {

    private final UserService userService;

    @PatchMapping(value = "/profile", consumes = {"multipart/form-data"})
    public ResponseEntity<UserResponse> updateProfile(
            Principal principal,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "zipCode", required = false) String zipCode,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "dateOfBirth", required = false) String dateOfBirth,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .address(address)
                .city(city)
                .state(state)
                .country(country)
                .zipCode(zipCode)
                .bio(bio)
                .dateOfBirth(dateOfBirth)
                .gender(gender)
                .build();

        String userEmail = principal.getName();
        return ResponseEntity.ok(userService.updateProfile(userEmail, request, image));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Principal principal) {
        return ResponseEntity.ok(userService.getUserByEmail(principal.getName()));
    }
}