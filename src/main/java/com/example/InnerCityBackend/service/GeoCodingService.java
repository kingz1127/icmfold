package com.example.InnerCityBackend.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class GeoCodingService {

    @Value("${google.maps.api-key}")
    private String apiKey;

    private GeoApiContext context;

    @PostConstruct
    public void init() {
        this.context = new GeoApiContext.Builder().apiKey(apiKey).build();
    }

    public double[] getCoordinates(String address) {
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
            if (results != null && results.length > 0) {
                return new double[]{results[0].geometry.location.lat, results[0].geometry.location.lng};
            }
            // Throw the custom exception if address is not found
            throw new GeoCodingException("Address not found on Google Maps");
        } catch (Exception e) {
            throw new GeoCodingException("Maps API Error: " + e.getMessage());
        }
    }

    // ADD THIS INNER CLASS
    public static class GeoCodingException extends RuntimeException {
        public GeoCodingException(String message) {
            super(message);
        }
    }


}