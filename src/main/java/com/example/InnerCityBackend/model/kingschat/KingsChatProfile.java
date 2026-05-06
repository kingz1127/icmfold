package com.example.InnerCityBackend.model.kingschat;

import com.example.InnerCityBackend.service.AuthService;

@lombok.Data
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class KingsChatProfile {
    private KingsChatUser user;
    private KingsChatEmail email;
    private String gender;
    private String phone_number;
    private String country_code;
    private boolean has_password;
    private long birth_date_in_millis;

    public String getPhoneNumber() { return phone_number; }
    public String getCountryCode() { return country_code; }
}
