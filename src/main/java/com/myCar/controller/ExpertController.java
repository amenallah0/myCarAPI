package com.myCar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;    // Pour Spring Boot 2.x
import com.myCar.exception.ResourceNotFoundException;

import com.myCar.domain.Expert;
import com.myCar.service.ExpertService;
import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalDate;
import com.myCar.dto.ExpertReportDTO;
import com.myCar.domain.ExpertReport;

@RestController
@RequestMapping("/api/experts")
public class ExpertController {
    
    @Autowired
    private ExpertService expertService;
    
    @PostMapping
    public ResponseEntity<Expert> createExpert(@Valid @RequestBody Expert expert) {
        Expert newExpert = expertService.createExpert(expert);
        return new ResponseEntity<>(newExpert, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Expert> updateExpert(@PathVariable Long id, @Valid @RequestBody Expert expert) {
        Expert updatedExpert = expertService.updateExpert(id, expert);
        return ResponseEntity.ok(updatedExpert);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpert(@PathVariable Long id) {
        expertService.deleteExpert(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Expert> getExpert(@PathVariable Long id) {
        Expert expert = expertService.getExpert(id);
        return ResponseEntity.ok(expert);
    }
    
    @GetMapping
    public ResponseEntity<List<Expert>> getAllExperts() {
        List<Expert> experts = expertService.getAllExperts();
        return ResponseEntity.ok(experts);
    }
    
    @GetMapping("/specialite/{specialite}")
    public ResponseEntity<List<Expert>> getExpertsBySpecialite(@PathVariable String specialite) {
        List<Expert> experts = expertService.getExpertsBySpecialite(specialite);
        return ResponseEntity.ok(experts);
    }
    
    @PostMapping(value = "/expertise-requests/{requestId}/submit-report", 
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitExpertReport(
            @PathVariable Long requestId,
            @ModelAttribute ExpertReportDTO reportDTO) {
        try {
            // Conversion de la date si nécessaire
            if (reportDTO.getExpertiseDate() == null) {
                reportDTO.setExpertiseDate(LocalDate.now());
            }
            
            ExpertReport report = expertService.submitReport(requestId, reportDTO);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 