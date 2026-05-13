

package com.example.InnerCityBackend.service;

import com.example.InnerCityBackend.exception.BusinessException;
import com.example.InnerCityBackend.model.dto.request.UpdateProfileRequest;
import com.example.InnerCityBackend.model.dto.response.UserResponse;
import com.example.InnerCityBackend.model.entity.User;
import com.example.InnerCityBackend.model.enums.Gender;
import com.example.InnerCityBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse updateProfile(String email, UpdateProfileRequest request, MultipartFile image) {
        try {
            log.info("Updating profile for user: {}", email);
            log.debug("Received dateOfBirth: {}", request.getDateOfBirth());

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessException("User not found"));

            // Update only the fields that are provided (not null)
            if (request.getFirstName() != null) {
                user.setFirstName(request.getFirstName().trim());
                log.debug("Updated firstName");
            }

            if (request.getLastName() != null) {
                user.setLastName(request.getLastName().trim());
                log.debug("Updated lastName");
            }

            if (request.getPhone() != null) {
                user.setPhone(request.getPhone().trim());
                log.debug("Updated phone");
            }

            if (request.getAddress() != null) {
                user.setAddress(request.getAddress().trim());
                log.debug("Updated address");
            }

            if (request.getCity() != null) {
                user.setCity(request.getCity().trim());
                log.debug("Updated city");
            }

            if (request.getState() != null) {
                user.setState(request.getState().trim());
                log.debug("Updated state");
            }

            if (request.getCountry() != null) {
                user.setCountry(request.getCountry().trim());
                log.debug("Updated country");
            }

            if (request.getZipCode() != null) {
                user.setZipCode(request.getZipCode().trim());
                log.debug("Updated zipCode");
            }

            if (request.getBio() != null) {
                user.setBio(request.getBio().trim());
                log.debug("Updated bio");
            }

            // Handle date of birth with multiple format support
            if (request.getDateOfBirth() != null && !request.getDateOfBirth().isEmpty()) {
                try {
                    LocalDate dateOfBirth = parseDate(request.getDateOfBirth());
                    user.setDateOfBirth(dateOfBirth);
                    log.debug("Updated dateOfBirth: {}", dateOfBirth);
                } catch (DateTimeParseException e) {
                    log.warn("Invalid date format: {}", request.getDateOfBirth());
                    throw new BusinessException("Invalid date format. Please use MM/dd/yyyy or yyyy-MM-dd");
                }
            }

            // Handle gender (only if provided and not empty)
            if (request.getGender() != null && !request.getGender().isEmpty()) {
                try {
                    Gender gender = Gender.valueOf(request.getGender().toUpperCase());
                    user.setGender(gender);
                    log.debug("Updated gender: {}", gender);
                } catch (IllegalArgumentException e) {
                    throw new BusinessException("Invalid gender value. Allowed values: MALE, FEMALE, OTHER");
                }
            }

            // Handle avatar image (only if provided)
            if (image != null && !image.isEmpty()) {
                try {
                    String avatarBase64 = processImage(image);
                    user.setAvatar(avatarBase64);
                    log.debug("Updated avatar image");
                } catch (Exception e) {
                    log.error("Failed to process image: {}", e.getMessage());
                    throw new BusinessException("Failed to process image: " + e.getMessage());
                }
            }

            User savedUser = userRepository.save(user);
            log.info("Successfully updated profile for user: {}", email);

            return mapToResponse(savedUser);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating profile for user {}: {}", email, e.getMessage(), e);
            throw new BusinessException("Failed to update profile: " + e.getMessage());
        }
    }

    /**
     * Parse date string from multiple possible formats
     * Supports: MM/dd/yyyy, M/dd/yyyy, MM/d/yyyy, M/d/yyyy, and yyyy-MM-dd
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new DateTimeParseException("Date string is null or empty", dateStr, 0);
        }

        // Define possible date formats that the frontend might send
        String[] possibleFormats = {
                "MM/dd/yyyy",
                "M/dd/yyyy",
                "MM/d/yyyy",
                "M/d/yyyy",
                "yyyy-MM-dd"
        };

        for (String format : possibleFormats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                LocalDate parsedDate = LocalDate.parse(dateStr.trim(), formatter);
                log.debug("Successfully parsed date '{}' using format '{}'", dateStr, format);
                return parsedDate;
            } catch (DateTimeParseException e) {
                // Try next format
                log.trace("Failed to parse '{}' with format '{}'", dateStr, format);
            }
        }

        // If all formats fail, throw exception with helpful message
        throw new DateTimeParseException(
                "Unable to parse date: " + dateStr + ". Please use format like 12/31/1990 or 1990-12-31",
                dateStr,
                0
        );
    }

    private String processImage(MultipartFile file) {
        try {
            // Validate file size (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new BusinessException("Image size should be less than 5MB");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/") && !contentType.equals("image/svg+xml"))) {
                throw new BusinessException("Only image files are allowed. Supported types: JPEG, PNG, GIF, SVG");
            }

            // Convert to Base64
            byte[] bytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(bytes);
            String result = "data:" + contentType + ";base64," + base64Image;

            log.debug("Image processed successfully. Size: {} bytes, Type: {}", bytes.length, contentType);
            return result;

        } catch (IOException e) {
            log.error("Failed to read image file: {}", e.getMessage());
            throw new BusinessException("Failed to read image file");
        }
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        try {
            log.debug("Fetching user by email: {}", email);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessException("User not found with email: " + email));

            return mapToResponse(user);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching user by email {}: {}", email, e.getMessage(), e);
            throw new BusinessException("Failed to fetch user: " + e.getMessage());
        }
    }

    private UserResponse mapToResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .country(user.getCountry())
                .zipCode(user.getZipCode())
                .bio(user.getBio())
                .avatar(user.getAvatar())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender() != null ? user.getGender().name() : null)
                .role(user.getRole() != null ? user.getRole().name() : null)
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}