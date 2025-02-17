package com.myCar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.myCar.domain.ExpertRequest;
import com.myCar.service.ExpertRequestService;
import com.myCar.dto.ExpertRequestDTO;
import com.myCar.exception.ResourceNotFoundException;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ExpertRequestService expertRequestService;

    @GetMapping("/expert-requests")
    public ResponseEntity<?> getAllExpertRequests() {
        try {
            List<ExpertRequestDTO> requests = expertRequestService.getAllRequests();
            if (requests.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la récupération des demandes: " + e.getMessage());
        }
    }

    @PutMapping("/expert-requests/{id}/approve")
    public ResponseEntity<?> approveExpertRequest(@PathVariable Long id) {
        try {
            ExpertRequest request = expertRequestService.approveRequest(id);
            return ResponseEntity.ok(request);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Demande d'expert non trouvée");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Une erreur est survenue lors de l'approbation");
        }
    }

    @PutMapping("/expert-requests/{id}/reject")
    public ResponseEntity<?> rejectExpertRequest(@PathVariable Long id) {
        try {
            ExpertRequest request = expertRequestService.rejectRequest(id);
            return ResponseEntity.ok(request);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Demande d'expert non trouvée");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Une erreur est survenue lors du rejet");
        }
    }
} 