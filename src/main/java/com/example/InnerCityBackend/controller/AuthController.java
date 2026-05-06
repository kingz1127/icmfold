package com.example.InnerCityBackend.controller;

import com.example.InnerCityBackend.model.dto.request.ForgotPasswordRequest;
import com.example.InnerCityBackend.model.dto.request.KingsChatAuthRequest;
import com.example.InnerCityBackend.model.dto.request.LoginRequest;
import com.example.InnerCityBackend.model.dto.request.SignupRequest;
import com.example.InnerCityBackend.model.dto.response.AuthResponse;
import com.example.InnerCityBackend.model.dto.response.SuccessResponse;
import com.example.InnerCityBackend.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = "User authentication and account management")

public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<SuccessResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.processForgotPassword(request.getEmail());
        return ResponseEntity.ok(new SuccessResponse("Reset link sent to your email"));
    }
    @PostMapping("/kingschat")
    public ResponseEntity<AuthResponse> kingschat(@RequestBody KingsChatAuthRequest request) {
        return ResponseEntity.ok(authService.signInWithKingsChat(request.getAccessToken()));
    }
}
