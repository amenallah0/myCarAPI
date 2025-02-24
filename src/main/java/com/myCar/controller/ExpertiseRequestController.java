package com.myCar.controller;

import com.myCar.domain.ExpertiseRequest;
import com.myCar.service.ExpertiseRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expertise-requests")
@CrossOrigin(origins = "*")
public class ExpertiseRequestController {

    @Autowired
    private ExpertiseRequestService expertiseRequestService;

    @PostMapping
    public ResponseEntity<ExpertiseRequest> createRequest(@RequestBody CreateExpertiseRequestDTO requestDTO) {
        ExpertiseRequest request = expertiseRequestService.createRequest(
            requestDTO.getUserId(),
            requestDTO.getExpertId(),
            requestDTO.getCarId(),
            requestDTO.getMessage()
        );
        return ResponseEntity.ok(request);
    }

    @GetMapping("/expert/{expertId}")
    public ResponseEntity<List<ExpertiseRequest>> getExpertRequests(@PathVariable Long expertId) {
        List<ExpertiseRequest> requests = expertiseRequestService.getRequestsByExpertId(expertId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpertiseRequest>> getUserRequests(@PathVariable Long userId) {
        List<ExpertiseRequest> requests = expertiseRequestService.getRequestsByUserId(userId);
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{requestId}/accept")
    public ResponseEntity<ExpertiseRequest> acceptRequest(@PathVariable Long requestId) {
        ExpertiseRequest request = expertiseRequestService.acceptRequest(requestId);
        return ResponseEntity.ok(request);
    }

    @PutMapping("/{requestId}/reject")
    public ResponseEntity<ExpertiseRequest> rejectRequest(@PathVariable Long requestId) {
        ExpertiseRequest request = expertiseRequestService.rejectRequest(requestId);
        return ResponseEntity.ok(request);
    }
}

class CreateExpertiseRequestDTO {
    private Long userId;
    private Long expertId;
    private Long carId;
    private String message;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getExpertId() { return expertId; }
    public void setExpertId(Long expertId) { this.expertId = expertId; }
    
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
} 