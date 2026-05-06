// Create this new file: com.example.InnerCityBackend.model.dto.request.KingsChatAuthRequest.java
package com.example.InnerCityBackend.model.dto.request;

import lombok.Data;

@Data
public class KingsChatAuthRequest {
    private String accessToken;
    private String refreshToken; // Optional, but good to have
}