package com.example.InnerCityBackend.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String authorizationUrl;
    private String reference;
    private String status;
}