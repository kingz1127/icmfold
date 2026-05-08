package com.example.InnerCityBackend.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data public class UpdateCountRequest {
    @NotNull @Min(0) Integer count;
}