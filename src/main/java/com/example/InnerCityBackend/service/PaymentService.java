//package com.example.InnerCityBackend.service;
//
//import com.example.InnerCityBackend.exception.BusinessException;
//import com.example.InnerCityBackend.model.dto.request.DonationRequest;
//import com.example.InnerCityBackend.model.dto.response.PaymentResponse;
//import com.example.InnerCityBackend.model.entity.Donation;
//import com.example.InnerCityBackend.repository.DonationRepository;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.RestTemplate;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class PaymentService {
//
//    private final DonationRepository donationRepository;
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @Value("${paystack.secret-key}")
//    private String paystackSecretKey;
//
//    @Value("${paystack.url.initialize}")
//    private String initializeUrl;
//
//    @Value("${paystack.url.verify}")
//    private String verifyUrl;
//
//    @Transactional
//    public PaymentResponse initializePayment(DonationRequest request) {
//        // Generate unique reference (Prevents double charging at DB level)
//        String reference = "ICM-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
//
//        // Convert to smallest currency unit (Kobo/Cents)
//        BigDecimal amountInSmallestUnit = request.getAmount().multiply(new BigDecimal(100));
//
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("email", request.getEmail());
//        payload.put("amount", amountInSmallestUnit.toBigInteger());
//        payload.put("currency", request.getCurrency().toUpperCase());
//        payload.put("reference", reference);
//        payload.put("callback_url", "https://icm-web-beta.vercel.app/donation/verify");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(paystackSecretKey);
//
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
//
//        try {
//            ResponseEntity<Map> response = restTemplate.postForEntity(initializeUrl, entity, Map.class);
//
//            if (response.getStatusCode() == HttpStatus.OK) {
//                // Save as PENDING
//                Donation donation = Donation.builder()
//                        .email(request.getEmail())
//                        .fullName(request.getFullName())
//                        .kingsChatFullName(request.getKingsChatFullName())
//                        .amount(request.getAmount())
//                        .currency(request.getCurrency().toUpperCase())
//                        .reference(reference)
//                        .status("PENDING")
//                        .subcategoryId(request.getSubcategoryId())
//                        .continent(request.getContinent())
//                        .build();
//                donationRepository.save(donation);
//
//                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
//                return PaymentResponse.builder()
//                        .authorizationUrl(data.get("authorization_url").toString())
//                        .reference(reference)
//                        .status("PENDING")
//                        .build();
//            }
//        } catch (Exception e) {
//            log.error("Paystack Init Error: {}", e.getMessage());
//            throw new BusinessException("Payment gateway unreachable.");
//        }
//        throw new BusinessException("Initialization failed");
//    }
//
//    @Transactional
//    public Donation verifyDonation(String reference) {
//        log.info("Verifying transaction: {}", reference);
//
//        Donation donation = donationRepository.findByReference(reference)
//                .orElseThrow(() -> new BusinessException("Transaction not found in our database."));
//
//        // If already SUCCESS, just return it
//        if ("SUCCESS".equalsIgnoreCase(donation.getStatus())) {
//            return donation;
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(paystackSecretKey);
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        try {
//            // Call Paystack Verify API
//            ResponseEntity<JsonNode> response = restTemplate.exchange(
//                    verifyUrl + reference, HttpMethod.GET, entity, JsonNode.class);
//
//            JsonNode body = response.getBody();
//            if (body != null && body.has("data")) {
//                String paystackStatus = body.get("data").get("status").asText();
//                log.info("Paystack API status for {}: {}", reference, paystackStatus);
//
//                // Update database based on Paystack response
//                if ("success".equalsIgnoreCase(paystackStatus)) {
//                    donation.setStatus("SUCCESS");
//                } else if ("failed".equalsIgnoreCase(paystackStatus)) {
//                    donation.setStatus("FAILED");
//                } else {
//                    donation.setStatus(paystackStatus.toUpperCase());
//                }
//
//                return donationRepository.save(donation);
//            }
//        } catch (Exception e) {
//            log.error("Verification failed for {}: {}", reference, e.getMessage());
//        }
//        return donation;
//    }
//
//    // Webhook processing logic
//    public void handleWebhook(String payload, String signature) {
//        try {
//            // Verify signature
//            String expected = hmacSHA512(payload, paystackSecretKey);
//            if (!expected.equals(signature)) return;
//
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode node = mapper.readTree(payload);
//            if ("charge.success".equals(node.get("event").asText())) {
//                String reference = node.get("data").get("reference").asText();
//                this.verifyDonation(reference); // reuse verify logic
//            }
//        } catch (Exception e) {
//            log.error("Webhook error: {}", e.getMessage());
//        }
//    }
//
//    private String hmacSHA512(String data, String key) throws Exception {
//        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
//        mac.init(new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA512"));
//        byte[] bytes = mac.doFinal(data.getBytes());
//        StringBuilder sb = new StringBuilder();
//        for (byte b : bytes) sb.append(String.format("%02x", b));
//        return sb.toString();
//    }
//
//    public List<Donation> getMyHistory(String name) {
//    }
//}



package com.example.InnerCityBackend.service;

import com.example.InnerCityBackend.exception.BusinessException;
import com.example.InnerCityBackend.model.dto.request.DonationRequest;
import com.example.InnerCityBackend.model.dto.response.PaymentResponse;
import com.example.InnerCityBackend.model.entity.Donation;
import com.example.InnerCityBackend.repository.DonationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final DonationRepository donationRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${paystack.secret-key}")
    private String paystackSecretKey;

    @Value("${paystack.url.initialize}")
    private String initializeUrl;

    @Value("${paystack.url.verify}")
    private String verifyUrl;

    @Transactional
    public PaymentResponse initializePayment(DonationRequest request) {
        String reference = "ICM-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        BigDecimal amountInSmallestUnit = request.getAmount().multiply(new BigDecimal(100));

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", request.getEmail());
        payload.put("amount", amountInSmallestUnit.toBigInteger());
        payload.put("currency", request.getCurrency().toUpperCase());
        payload.put("reference", reference);
        // Ensure this URL matches your frontend verification route
        payload.put("callback_url", "https://icm-web-beta.vercel.app/donation/verify");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(paystackSecretKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(initializeUrl, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                Donation donation = Donation.builder()
                        .email(request.getEmail())
                        .fullName(request.getFullName())
                        .kingsChatFullName(request.getKingsChatFullName())
                        .amount(request.getAmount())
                        .currency(request.getCurrency().toUpperCase())
                        .reference(reference)
                        .status("PENDING")
                        .subcategoryId(request.getSubcategoryId())
                        .continent(request.getContinent())
                        .build();
                donationRepository.save(donation);

                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                return PaymentResponse.builder()
                        .authorizationUrl(data.get("authorization_url").toString())
                        .reference(reference)
                        .status("PENDING")
                        .build();
            }
        } catch (Exception e) {
            log.error("Paystack Init Error: {}", e.getMessage());
            throw new BusinessException("Payment gateway unreachable.");
        }
        throw new BusinessException("Initialization failed");
    }

    @Transactional
    public Donation verifyDonation(String reference) {
        log.info("Verifying transaction in DB: {}", reference);

        Donation donation = donationRepository.findByReference(reference)
                .orElseThrow(() -> new BusinessException("Transaction not found."));

        // If already SUCCESS, return immediately
        if ("SUCCESS".equalsIgnoreCase(donation.getStatus())) {
            return donation;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(paystackSecretKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // URL logic: make sure your verifyUrl in YAML ends with /
            String finalUrl = verifyUrl.endsWith("/") ? verifyUrl + reference : verifyUrl + "/" + reference;

            ResponseEntity<JsonNode> response = restTemplate.exchange(finalUrl, HttpMethod.GET, entity, JsonNode.class);

            JsonNode body = response.getBody();
            if (body != null && body.has("data")) {
                String paystackStatus = body.get("data").get("status").asText();
                log.info("Paystack Real-time status for {}: {}", reference, paystackStatus);

                if ("success".equalsIgnoreCase(paystackStatus)) {
                    donation.setStatus("SUCCESS");
                } else if ("failed".equalsIgnoreCase(paystackStatus)) {
                    donation.setStatus("FAILED");
                } else {
                    donation.setStatus(paystackStatus.toUpperCase());
                }
                // Save the updated status to database
                return donationRepository.save(donation);
            }
        } catch (Exception e) {
            log.error("Verification failed for {}: {}", reference, e.getMessage());
        }
        return donation;
    }

    public void handleWebhook(String payload, String signature) {
        try {
            String expected = hmacSHA512(payload, paystackSecretKey);
            if (!expected.equals(signature)) {
                log.warn("Webhook signature mismatch!");
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(payload);
            String event = node.get("event").asText();

            if ("charge.success".equals(event)) {
                String reference = node.get("data").get("reference").asText();
                this.verifyDonation(reference);
            }
        } catch (Exception e) {
            log.error("Webhook error: {}", e.getMessage());
        }
    }

    private String hmacSHA512(String data, String key) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
        mac.init(new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA512"));
        byte[] bytes = mac.doFinal(data.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    // FIXED: Implementation for History
    public List<Donation> getMyHistory(String email) {
        return donationRepository.findByEmailOrderByCreatedAtDesc(email);
    }
}