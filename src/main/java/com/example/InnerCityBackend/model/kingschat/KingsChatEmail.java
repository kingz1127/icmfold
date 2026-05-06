package com.example.InnerCityBackend.model.kingschat;

@lombok.Data
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class KingsChatEmail {
    private String address;
    private boolean verified;
}
