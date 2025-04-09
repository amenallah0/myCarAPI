package com.myCar.service;

import com.myCar.domain.ExpertiseRequest;
import com.myCar.domain.User;
import com.myCar.domain.Car;
import com.myCar.domain.ExpertReport;
import com.myCar.dto.ExpertReportDTO;
import com.myCar.repository.ExpertiseRequestRepository;
import com.myCar.repository.UserRepository;
import com.myCar.repository.CarRepository;
import com.myCar.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpertiseRequestService {

    @Autowired
    private ExpertiseRequestRepository expertiseRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    public ExpertiseRequest createRequest(Long userId, Long expertId, Long carId, String message) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        User expert = userRepository.findById(expertId)
            .orElseThrow(() -> new ResourceNotFoundException("Expert not found"));
        
        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        ExpertiseRequest request = new ExpertiseRequest();
        request.setUser(user);
        request.setExpert(expert);
        request.setCar(car);
        request.setMessage(message);
        
        return expertiseRequestRepository.save(request);
    }

    public List<ExpertiseRequest> getRequestsByExpertId(Long expertId) {
        return expertiseRequestRepository.findByExpertId(expertId);
    }

    public List<ExpertiseRequest> getRequestsByUserId(Long userId) {
        return expertiseRequestRepository.findByUserId(userId);
    }

    public ExpertiseRequest acceptRequest(Long requestId) {
        ExpertiseRequest request = expertiseRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        request.setStatus(ExpertiseRequest.RequestStatus.ACCEPTED);
        return expertiseRequestRepository.save(request);
    }

    public ExpertiseRequest rejectRequest(Long requestId) {
        ExpertiseRequest request = expertiseRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        request.setStatus(ExpertiseRequest.RequestStatus.REJECTED);
        return expertiseRequestRepository.save(request);
    }

    public ExpertiseRequest getExpertiseRequestById(Long id) {
        return expertiseRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Expertise request not found"));
    }
    
    public ExpertiseRequest submitReport(Long requestId, ExpertReportDTO reportDTO) throws IOException {
        ExpertiseRequest request = getExpertiseRequestById(requestId);
        
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
        
        request.setReport(report);
        request.setStatus(ExpertiseRequest.RequestStatus.COMPLETED);
        
        return expertiseRequestRepository.save(request);
    }

    public ExpertReport getReportById(Long requestId) {
        ExpertiseRequest request = expertiseRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Expertise request not found"));
        
        if (request.getReport() == null) {
            throw new ResourceNotFoundException("Report not found for this request");
        }
        
        return request.getReport();
    }

    public void promoteAnnonce(Long annonceId) {
        // Récupérer l'annonce (car) par son ID
        Car car = carRepository.findById(annonceId)
            .orElseThrow(() -> new ResourceNotFoundException("Annonce not found with id: " + annonceId));

        // Mettre à jour le statut de promotion
        car.setPromoted(true);
        
        // Définir la date de début de promotion
        car.setPromotionStartDate(LocalDateTime.now());
        
        // Définir la date de fin de promotion (par exemple, 7 jours)
        car.setPromotionEndDate(LocalDateTime.now().plusDays(7));

        // Sauvegarder les modifications
        carRepository.save(car);
    }

    public List<ExpertReport> getReportsByUserId(Long userId) {
        List<ExpertiseRequest> requests = expertiseRequestRepository.findByUserId(userId);
        return requests.stream()
            .filter(request -> request.getReport() != null)
            .map(ExpertiseRequest::getReport)
            .collect(Collectors.toList());
    }
} 