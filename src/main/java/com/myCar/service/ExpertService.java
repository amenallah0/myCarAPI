package com.myCar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myCar.domain.Expert;
import com.myCar.domain.ExpertReport;
import com.myCar.domain.ExpertiseRequest;
import com.myCar.dto.ExpertReportDTO;
import com.myCar.repository.ExpertRepository;
import com.myCar.repository.ExpertReportRepository;
import com.myCar.repository.ExpertiseRequestRepository;
import com.myCar.exception.ResourceNotFoundException;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class ExpertService {
    
    private final ExpertRepository expertRepository;
    private final ExpertReportRepository expertReportRepository;
    private final ExpertiseRequestRepository expertiseRequestRepository;
    
    @Autowired
    public ExpertService(ExpertRepository expertRepository, ExpertReportRepository expertReportRepository, ExpertiseRequestRepository expertiseRequestRepository) {
        this.expertRepository = expertRepository;
        this.expertReportRepository = expertReportRepository;
        this.expertiseRequestRepository = expertiseRequestRepository;
    }
    
    public Expert createExpert(Expert expert) {
        return expertRepository.save(expert);
    }
    
    public Expert updateExpert(Long id, Expert expert) {
        Expert existingExpert = expertRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Expert non trouvé avec l'id : " + id));
            
        existingExpert.setNom(expert.getNom());
        existingExpert.setPrenom(expert.getPrenom());
        existingExpert.setSpecialite(expert.getSpecialite());
        existingExpert.setEmail(expert.getEmail());
        existingExpert.setTelephone(expert.getTelephone());
        
        return expertRepository.save(existingExpert);
    }
    
    public void deleteExpert(Long id) {
        Expert expert = expertRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Expert non trouvé avec l'id : " + id));
        expertRepository.delete(expert);
    }
    
    public Expert getExpert(Long id) {
        return expertRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Expert non trouvé avec l'id : " + id));
    }
    
    public List<Expert> getAllExperts() {
        return expertRepository.findAll();
    }
    
    public List<Expert> getExpertsBySpecialite(String specialite) {
        return expertRepository.findBySpecialite(specialite);
    }
    
    @Transactional
    public ExpertReport submitReport(Long requestId, ExpertReportDTO reportDTO) {
        try {
            ExpertiseRequest request = expertiseRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Expertise request not found"));

            // Check if a report already exists for this request
            ExpertReport existingReport = expertReportRepository.findByExpertiseRequestId(requestId)
                .stream()
                .findFirst()
                .orElse(null);

            ExpertReport report;
            if (existingReport != null) {
                // Update existing report
                report = existingReport;
            } else {
                // Create new report
                report = new ExpertReport();
                report.setExpertiseRequest(request);
                report.setUser(request.getUser());
            }

            // Update report fields
            report.setTitle(reportDTO.getTitle());
            report.setCriticalData(reportDTO.getCriticalData());
            report.setExpertiseDate(reportDTO.getExpertiseDate());
            report.setMessage(reportDTO.getMessage());
            report.setExpertName(reportDTO.getExpertName());
            report.setExpertEmail(reportDTO.getExpertEmail());
            report.setExpertPhone(reportDTO.getExpertPhone());
            
            if (reportDTO.getFile() != null && !reportDTO.getFile().isEmpty()) {
                report.setFileName(reportDTO.getFile().getOriginalFilename());
                report.setFileType(reportDTO.getFile().getContentType());
                report.setFileData(reportDTO.getFile().getBytes());
            }
            
            // Update request status
            request.setStatus(ExpertiseRequest.RequestStatus.COMPLETED);
            expertiseRequestRepository.save(request);
            
            return expertReportRepository.save(report);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du rapport", e);
        }
    }

    private void updateExistingReport(ExpertReport existingReport, ExpertReportDTO reportDTO) {
        existingReport.setTitle(reportDTO.getTitle());
        existingReport.setCriticalData(reportDTO.getCriticalData());
        existingReport.setExpertiseDate(reportDTO.getExpertiseDate());
        existingReport.setMessage(reportDTO.getMessage());
        existingReport.setExpertName(reportDTO.getExpertName());
        existingReport.setExpertEmail(reportDTO.getExpertEmail());
        existingReport.setExpertPhone(reportDTO.getExpertPhone());
        
        try {
            if (reportDTO.getFile() != null && !reportDTO.getFile().isEmpty()) {
                existingReport.setFileName(reportDTO.getFile().getOriginalFilename());
                existingReport.setFileType(reportDTO.getFile().getContentType());
                existingReport.setFileData(reportDTO.getFile().getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error updating file data", e);
        }
    }

    @Transactional(readOnly = true)
    public ExpertReport getReportByRequestId(Long requestId) {
        try {
            List<ExpertReport> reports = expertReportRepository.findByExpertiseRequestId(requestId);
            if (reports.isEmpty()) {
                throw new ResourceNotFoundException("Rapport non trouvé pour la demande d'expertise: " + requestId);
            }
            return reports.get(0);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération du rapport", e);
        }
    }
} 