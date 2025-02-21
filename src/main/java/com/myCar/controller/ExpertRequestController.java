package com.myCar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.myCar.domain.ExpertRequest;
import com.myCar.dto.ExpertRequestDTO;
import com.myCar.service.ExpertRequestService;
@RestController
@RequestMapping("/api/expert-requests")
public class ExpertRequestController {

    @Autowired
    private ExpertRequestService expertRequestService;

    @PostMapping
    public ResponseEntity<ExpertRequest> createRequest(
            @RequestParam("userId") Long userId,
            @RequestParam("specialization") String specialization,
            @RequestParam("experience") String experience,
            @RequestParam("currentPosition") String currentPosition,
            @RequestParam("diploma") MultipartFile diploma) {
        
        ExpertRequest request = expertRequestService.createRequest(
            userId, specialization, experience, currentPosition, diploma);
        return ResponseEntity.ok(request);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ExpertRequest> approveRequest(@PathVariable Long id) {
        ExpertRequest request = expertRequestService.approveRequest(id);
        return ResponseEntity.ok(request);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ExpertRequest> rejectRequest(@PathVariable Long id) {
        ExpertRequest request = expertRequestService.rejectRequest(id);
        return ResponseEntity.ok(request);
    }

    @GetMapping
    public ResponseEntity<List<ExpertRequestDTO>> getAllRequests() {
        List<ExpertRequestDTO> requests = expertRequestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteRequestsByUserId(@PathVariable Long userId) {
        try {
            expertRequestService.deleteRequestsByUserId(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la suppression des demandes: " + e.getMessage());
        }
    }
} 