package com.myCar.controller;

import com.myCar.service.FlouciService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private FlouciService flouciService;

    @PostMapping("/generate-link")
    public ResponseEntity<?> generatePaymentLink(@RequestBody Map<String, Object> request) {
        try {
            int amount = Integer.parseInt(request.get("amount").toString());
            String trackingId = request.get("trackingId").toString();
            String successUrl = request.get("successUrl").toString();
            String failUrl = request.get("failUrl").toString();
            
            Map<String, Object> response = flouciService.generatePaymentLink(amount, trackingId, successUrl, failUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generating payment link: " + e.getMessage());
        }
    }
} 