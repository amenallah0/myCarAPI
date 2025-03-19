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
    
    public Map<String, Object> generatePaymentLink(int amount, String trackingId, String successUrl, String failUrl) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("app_token", appToken);
        requestBody.put("app_secret", appSecret);
        requestBody.put("amount", amount);
        requestBody.put("accept_card", "true");
        requestBody.put("session_timeout_secs", 1200);
        requestBody.put("success_link", successUrl);
        requestBody.put("fail_link", failUrl);
        requestBody.put("developer_tracking_id", trackingId);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        return restTemplate.postForObject(FLOUCI_API_URL, request, Map.class);
    }
} 