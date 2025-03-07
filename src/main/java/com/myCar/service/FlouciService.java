package com.myCar.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class FlouciService {

    @Value("${flouci.app.token}")
    private String appToken;

    @Value("${flouci.app.secret}")
    private String appSecret;

    private final String FLOUCI_API_URL = "https://developers.flouci.com/api/generate_payment";
    
    public Map<String, Object> generatePaymentLink(int amount, String trackingId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("app_token", appToken);
        paymentData.put("app_secret", appSecret);
        paymentData.put("accept_card", true);
        paymentData.put("amount", amount);
        paymentData.put("success_link", "http://localhost:3000/payment-success");
        paymentData.put("fail_link", "http://localhost:3000/payment-failed");
        paymentData.put("session_timeout_secs", 1200);
        paymentData.put("developer_tracking_id", trackingId);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(paymentData, headers);
        
        return restTemplate.postForObject(FLOUCI_API_URL, request, Map.class);
    }
} 