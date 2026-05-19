package com.example.InnerCityBackend.controller;

import com.example.InnerCityBackend.model.dto.request.DonationRequest;
import com.example.InnerCityBackend.model.dto.response.PaymentResponse;
import com.example.InnerCityBackend.model.entity.Donation;
import com.example.InnerCityBackend.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Added for logging
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("donations")
@RequiredArgsConstructor
@Slf4j // Added this
@Tag(name = "Donation Controller", description = "Handles Paystack payments")
public class DonationController {

    private final PaymentService paymentService;

    @Value("${paystack.secret-key}")  // <-- ADD THIS
    private String paystackSecretKey;

    @PostMapping("/initialize")
    public ResponseEntity<PaymentResponse> initialize(@Valid @RequestBody DonationRequest request) {
        return ResponseEntity.ok(paymentService.initializePayment(request));
    }

    // This is called by your React Native app after the payment screen closes
    @GetMapping("/verify/{reference}")
    public ResponseEntity<Donation> verify(@PathVariable String reference) {
        return ResponseEntity.ok(paymentService.verifyDonation(reference));
    }

    @GetMapping("/my-history")
    public ResponseEntity<List<Donation>> getHistory(Principal principal) {
        return ResponseEntity.ok(paymentService.getMyHistory(principal.getName()));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody String payload,
                                        @RequestHeader("x-paystack-signature") String signature) {
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }
}