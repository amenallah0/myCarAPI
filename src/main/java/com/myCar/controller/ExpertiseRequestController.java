package com.myCar.controller;

import com.myCar.domain.ExpertReport;
import com.myCar.domain.ExpertiseRequest;
import com.myCar.dto.ExpertReportDTO;
import com.myCar.service.ExpertiseRequestService;
import com.myCar.service.ExpertService;
import com.myCar.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.Data;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expertise-requests")
@CrossOrigin(origins = "*")
public class ExpertiseRequestController {

    @Autowired
    private ExpertiseRequestService expertiseRequestService;

    @Autowired
    private ExpertService expertService;

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
    public ResponseEntity<?> getExpertRequests(@PathVariable Long expertId) {
        try {
            List<ExpertiseRequest> requests = expertiseRequestService.getRequestsByExpertId(expertId);
            return ResponseEntity.ok(requests);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Expert non trouvé", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erreur lors du chargement des expertises", e.getMessage()));
        }
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

    @GetMapping("/{id}")
    public ResponseEntity<ExpertiseRequest> getExpertiseRequest(@PathVariable Long id) {
        try {
            ExpertiseRequest request = expertiseRequestService.getExpertiseRequestById(id);
            return ResponseEntity.ok(request);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/{requestId}/submit-report", 
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> submitExpertReport(
            @PathVariable Long requestId,
            @ModelAttribute ExpertReportDTO reportDTO) {
        try {
            if (reportDTO.getExpertiseDate() == null) {
                reportDTO.setExpertiseDate(LocalDate.now());
            }
            
            ExpertReport report = expertService.submitReport(requestId, reportDTO);
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(report);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/{id}/download-file")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        ExpertReport report = expertiseRequestService.getReportById(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(report.getFileType()))
                .body(report.getFileData());
    }

    @PostMapping("/promote/{annonceId}")
    public ResponseEntity<?> promoteAnnonce(@PathVariable Long annonceId) {
        try {
            // Mettre à jour le statut de promotion de l'annonce
            expertiseRequestService.promoteAnnonce(annonceId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la promotion de l'annonce");
        }
    }

    @GetMapping("/{requestId}/report/download")
    public ResponseEntity<?> downloadReport(@PathVariable Long requestId) {
        try {
            ExpertReport report = expertService.getReportByRequestId(requestId);
            
            if (report == null || report.getFileData() == null) {
                return ResponseEntity.notFound().build();
            }

            // Vérifier le type de fichier
            String contentType = report.getFileType() != null ? 
                report.getFileType() : MediaType.APPLICATION_PDF_VALUE;

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(report.getFileData().length)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + (report.getFileName() != null ? 
                    report.getFileName() : "rapport-" + requestId + ".pdf") + "\"")
                .body(report.getFileData());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace(); // Pour le débogage
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erreur lors du téléchargement du rapport", e.getMessage()));
        }
    }

    @Data
    @AllArgsConstructor
    static class ErrorResponse {
        private String message;
        private String details;
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