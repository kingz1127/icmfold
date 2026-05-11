package com.example.InnerCityBackend.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    private String firstName; // JacksonConfig handles 'first_name' -> 'firstName'
    @NotBlank
    private String lastName;

    @Email
    @NotBlank private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    private String phone;

    @NotBlank
    private String gender;

    private String role = "user";

    @NotBlank
    private String country;
}
