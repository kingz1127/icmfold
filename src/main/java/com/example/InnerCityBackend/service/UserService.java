//package com.example.InnerCityBackend.service;
//
//import com.example.InnerCityBackend.exception.BusinessException;
//import com.example.InnerCityBackend.model.dto.request.UpdateProfileRequest;
//import com.example.InnerCityBackend.model.dto.response.UserResponse;
//import com.example.InnerCityBackend.model.entity.User;
//import com.example.InnerCityBackend.model.enums.Gender;
//import com.example.InnerCityBackend.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.Base64;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class UserService {
//
//    private final UserRepository userRepository;
//
//    @Transactional
//    public UserResponse updateProfile(String email, UpdateProfileRequest request, MultipartFile image) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new BusinessException("User not found"));
//
//        // Update fields if provided
//        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
//        if (request.getLastName() != null) user.setLastName(request.getLastName());
//        if (request.getPhone() != null) user.setPhone(request.getPhone());
//        if (request.getAddress() != null) user.setAddress(request.getAddress());
//        if (request.getCity() != null) user.setCity(request.getCity());
//        if (request.getState() != null) user.setState(request.getState());
//        if (request.getCountry() != null) user.setCountry(request.getCountry());
//        if (request.getZipCode() != null) user.setZipCode(request.getZipCode());
//        if (request.getBio() != null) user.setBio(request.getBio());
//
//        // Convert String to LocalDate
//        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isEmpty()) {
//            try {
//                LocalDate dateOfBirth = LocalDate.parse(request.getDateOfBirth());
//                user.setDateOfBirth(dateOfBirth);
//            } catch (Exception e) {
//                throw new BusinessException("Invalid date format. Please use yyyy-MM-dd");
//            }
//        }
//
//        // Convert String to Gender Enum
//        if (request.getGender() != null && !request.getGender().isEmpty()) {
//            try {
//                Gender gender = Gender.valueOf(request.getGender().toUpperCase());
//                user.setGender(gender);
//            } catch (IllegalArgumentException e) {
//                throw new BusinessException("Invalid gender value. Allowed values: MALE, FEMALE, OTHER");
//            }
//        }
//
//        // Handle avatar image
//        if (image != null && !image.isEmpty()) {
//            user.setAvatar(processImage(image));
//        }
//
//        return mapToResponse(userRepository.save(user));
//    }
//
//    private String processImage(MultipartFile file) {
//        try {
//            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
//            return "data:" + file.getContentType() + ";base64," + base64Image;
//        } catch (IOException e) {
//            throw new BusinessException("Failed to upload image file");
//        }
//    }
//
//    public UserResponse getUserByEmail(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new BusinessException("User not found"));
//        return mapToResponse(user);
//    }
//
//    private UserResponse mapToResponse(User user) {
//        return UserResponse.builder()
//                .id(user.getId())
//                .email(user.getEmail())
//                .firstName(user.getFirstName())
//                .lastName(user.getLastName())
//                .phone(user.getPhone())
//                .address(user.getAddress())
//                .city(user.getCity())
//                .state(user.getState())
//                .country(user.getCountry())
//                .zipCode(user.getZipCode())
//                .bio(user.getBio())
//                .avatar(user.getAvatar())
//                .dateOfBirth(LocalDate.parse(Optional.ofNullable(user.getDateOfBirth()).map(LocalDate::toString).orElse(null)))
//                .gender(Optional.ofNullable(user.getGender()).map(Enum::toString).orElse(null))
//                .role(Optional.ofNullable(user.getRole()).map(Enum::name).orElse(null))
//                .emailVerified(user.isEmailVerified())
//                .createdAt(user.getCreatedAt())
//                .updatedAt(user.getUpdatedAt())
//                .build();
//    }
//}


package com.example.InnerCityBackend.service;

import com.example.InnerCityBackend.exception.BusinessException;
import com.example.InnerCityBackend.model.dto.request.UpdateProfileRequest;
import com.example.InnerCityBackend.model.dto.response.UserResponse;
import com.example.InnerCityBackend.model.entity.User;
import com.example.InnerCityBackend.model.enums.Gender;
import com.example.InnerCityBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse updateProfile(String email, UpdateProfileRequest request, MultipartFile image) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getState() != null) user.setState(request.getState());
        if (request.getCountry() != null) user.setCountry(request.getCountry());
        if (request.getZipCode() != null) user.setZipCode(request.getZipCode());
        if (request.getBio() != null) user.setBio(request.getBio());

        // Convert String to LocalDate
        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isEmpty()) {
            try {
                user.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
            } catch (Exception e) {
                throw new BusinessException("Invalid date format. Please use yyyy-MM-dd");
            }
        }

        // Convert String to Gender Enum
        if (request.getGender() != null && !request.getGender().isEmpty()) {
            try {
                user.setGender(Gender.valueOf(request.getGender().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Invalid gender value. Allowed values: MALE, FEMALE, OTHER");
            }
        }

        // Handle avatar image
        if (image != null && !image.isEmpty()) {
            user.setAvatar(processImage(image));
        }

        return mapToResponse(userRepository.save(user));
    }

    private String processImage(MultipartFile file) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            return "data:" + file.getContentType() + ";base64," + base64Image;
        } catch (IOException e) {
            throw new BusinessException("Failed to upload image file");
        }
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));
        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
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
                .gender(user.getGender() != null ? user.getGender().toString() : null)
                .role(user.getRole() != null ? user.getRole().name() : null)
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}