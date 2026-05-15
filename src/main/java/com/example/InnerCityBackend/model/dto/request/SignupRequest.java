package com.example.InnerCityBackend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank
    @JsonProperty("last_name")
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
