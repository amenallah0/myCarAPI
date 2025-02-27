package com.myCar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myCar.domain.Expert;
import com.myCar.domain.ExpertReport;
import com.myCar.dto.ExpertReportDTO;
import com.myCar.repository.ExpertRepository;
import com.myCar.repository.ExpertReportRepository;
import com.myCar.exception.ResourceNotFoundException;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class ExpertService {
    
    private final ExpertRepository expertRepository;
    private final ExpertReportRepository expertReportRepository;
    
    @Autowired
    public ExpertService(ExpertRepository expertRepository, ExpertReportRepository expertReportRepository) {
        this.expertRepository = expertRepository;
        this.expertReportRepository = expertReportRepository;
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
    
    public ExpertReport submitReport(Long requestId, ExpertReportDTO reportDTO) throws IOException {
        // Créer un nouveau rapport
        ExpertReport report = new ExpertReport();
        report.setTitle(reportDTO.getTitle());
        report.setCriticalData(reportDTO.getCriticalData());
        report.setExpertiseDate(reportDTO.getExpertiseDate());
        report.setMessage(reportDTO.getMessage());
        report.setExpertName(reportDTO.getExpertName());
        report.setExpertEmail(reportDTO.getExpertEmail());
        report.setExpertPhone(reportDTO.getExpertPhone());
        
        // Gestion du fichier
        if (reportDTO.getFile() != null && !reportDTO.getFile().isEmpty()) {
            report.setFileName(reportDTO.getFile().getOriginalFilename());
            report.setFileType(reportDTO.getFile().getContentType());
            report.setFileData(reportDTO.getFile().getBytes());
        }
        
        return expertReportRepository.save(report);
    }
} 