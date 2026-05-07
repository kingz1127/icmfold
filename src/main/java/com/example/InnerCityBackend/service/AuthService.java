package com.example.InnerCityBackend.service;

import com.example.InnerCityBackend.exception.BusinessException;
import com.example.InnerCityBackend.model.dto.request.*;
import com.example.InnerCityBackend.model.dto.response.AuthResponse;
import com.example.InnerCityBackend.model.dto.response.UserResponse;
import com.example.InnerCityBackend.model.entity.Otp;
import com.example.InnerCityBackend.model.entity.User;
import com.example.InnerCityBackend.model.enums.Gender;
import com.example.InnerCityBackend.model.enums.UserRole;
import com.example.InnerCityBackend.model.kingschat.KingsChatProfile;
import com.example.InnerCityBackend.repository.OtpRepository;
import com.example.InnerCityBackend.repository.UserRepository;
import com.example.InnerCityBackend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Transactional
    public AuthResponse signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email is already registered");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .gender(Gender.valueOf(request.getGender().toUpperCase()))
                .role(UserRole.valueOf(request.getRole().toUpperCase()))
                .country(request.getCountry())
                .provider("EMAIL")
                .emailVerified(false)
                .build();

        User savedUser = userRepository.save(user);

        // 3. Generate JWT Token
        String token = jwtService.generateToken(savedUser);

        return buildAuthResponse(savedUser, token, "Signup successful");
    }

    public AuthResponse login(LoginRequest request) {
        // 1. Authenticate using Spring Security
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new BusinessException("Invalid email or password");
        }

        // 2. Fetch User and Generate Token
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found"));

        String token = jwtService.generateToken(user);

        return buildAuthResponse(user, token, "Login successful");
    }


    @Transactional
    public AuthResponse signInWithKingsChat(String kingschatAccessToken) {
        try {
            // 1. Verify token with KingsChat API and get user profile
            KingsChatProfile profile = verifyKingsChatToken(kingschatAccessToken);

            if (profile == null || profile.getUser() == null || profile.getUser().getUserId() == null) {
                throw new BusinessException("Invalid KingsChat token");
            }

            String kcUserId = profile.getUser().getUserId();
            String kingschatEmail = profile.getEmail() != null ? profile.getEmail().getAddress() : null;

            // 2. Check if user exists by KingsChat ID
            User user = userRepository.findByKingschatId(kcUserId).orElse(null);

            // 3. If not found by KingsChat ID, try by email
            if (user == null && kingschatEmail != null) {
                user = userRepository.findByEmail(kingschatEmail).orElse(null);
            }

            // 4. Create or update user
            if (user != null) {
                // Update existing user with KingsChat info
                user.setKingschatId(kcUserId);

                if (user.getFirstName() == null && profile.getUser().getName() != null) {
                    // Split name into first and last name if possible
                    String fullName = profile.getUser().getName();
                    String[] nameParts = fullName.split(" ", 2);
                    user.setFirstName(nameParts[0]);
                    if (nameParts.length > 1) {
                        user.setLastName(nameParts[1]);
                    }
                }

                if (user.getAvatar() == null && profile.getUser().getAvatarUrl() != null) {
                    user.setAvatar(profile.getUser().getAvatarUrl());
                }

                if (user.getBio() == null && profile.getUser().getUserBio() != null) {
                    user.setBio(profile.getUser().getUserBio());
                }

                user.setProvider("KINGSCHAT");
                user.setEmailVerified(true);

                // Update email if not set (for users who signed up with email first)
                if (user.getEmail() == null && kingschatEmail != null) {
                    user.setEmail(kingschatEmail);
                }
            } else {
                // Create new user from KingsChat profile
                String fullName = profile.getUser().getName();
                String firstName = fullName;
                String lastName = "";

                if (fullName != null && fullName.contains(" ")) {
                    String[] nameParts = fullName.split(" ", 2);
                    firstName = nameParts[0];
                    lastName = nameParts.length > 1 ? nameParts[1] : "";
                }

                user = User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(kingschatEmail)
                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .phone(profile.getPhoneNumber())
                        .gender(Gender.OTHER)
                        .role(UserRole.USER)
                        .country(profile.getCountryCode())
                        .kingschatId(kcUserId)
                        .provider("KINGSCHAT")
                        .avatar(profile.getUser().getAvatarUrl())
                        .bio(profile.getUser().getUserBio())
                        .emailVerified(true)
                        .build();
            }

            User savedUser = userRepository.save(user);


            String token = jwtService.generateToken(savedUser);

            return buildAuthResponse(savedUser, token, "KingsChat login successful");

        } catch (Exception e) {
            throw new BusinessException("KingsChat authentication failed: " + e.getMessage());
        }
    }

    private KingsChatProfile verifyKingsChatToken(String accessToken) {
        try {
            String url = "https://connect.kingsch.at/api/profile";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            System.out.println("KingsChat API response: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());


                System.out.println("Root keys: " + root.fieldNames());

                JsonNode profileNode = root.get("profile");

                // ADD THIS - check if profile node exists
                if (profileNode == null) {
                    System.out.println("No 'profile' key found. Full response: " + root.toPrettyString());
                    // Try parsing root directly
                    return objectMapper.treeToValue(root, KingsChatProfile.class);
                }

                return objectMapper.treeToValue(profileNode, KingsChatProfile.class);
            }

            return null;
        } catch (Exception e) {
            System.out.println("KingsChat verify error: " + e.getMessage());
            e.printStackTrace();
            throw new BusinessException("Failed to verify KingsChat token: " + e.getMessage());
        }
    }

    @Transactional
    public void processForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User with this email does not exist"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // MOCK: Print to console. In production, send this via email service.
//        System.out.println("DEBUG: Reset token for " + email + " is: " + token);
    }

    @Transactional
    public void sendOtp(SendOTPRequest request) {
        String email = request.getEmail();
        String code = String.format("%06d", new Random().nextInt(999999));


        otpRepository.deleteByEmail(email); // Clear previous codes

        Otp otp = Otp.builder()
                .email(email)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        otpRepository.save(otp);
        System.out.println("DEBUG: OTP for " + email + " is: " + code);
    }

    public void verifyOtp(VerifyOTPRequest request) {
        Otp otp = otpRepository.findByEmailAndCode(request.getEmail(), request.getOtp())
                .orElseThrow(() -> new BusinessException("Invalid OTP code"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("OTP has expired");
        }

        otpRepository.delete(otp); // Code used, delete it
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new BusinessException("Invalid or expired reset token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }


    private AuthResponse buildAuthResponse(User user, String token, String message) {
        UserResponse userDto = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .gender(user.getGender().name())
                .country(user.getCountry())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        return AuthResponse.builder()
                .message(message)
                .token(token)
                .user(userDto)
                .build();
    }


}