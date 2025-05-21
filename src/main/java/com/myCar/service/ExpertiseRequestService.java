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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
public class ExpertiseRequestService {

    private static final Logger logger = LoggerFactory.getLogger(ExpertiseRequestService.class);

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

    @Transactional(readOnly = true)
    public List<ExpertiseRequest> getRequestsByExpertId(Long expertId) {
        return expertiseRequestRepository.findByExpertId(expertId);
    }

    @Transactional(readOnly = true)
    public List<ExpertiseRequest> getRequestsByUserId(Long userId) {
        logger.info("Fetching requests for user ID: {}", userId);
        List<ExpertiseRequest> requests = expertiseRequestRepository.findByUserId(userId);
        logger.info("Found {} requests for user {}", requests.size(), userId);
        
        // Log détaillé de chaque requête
        requests.forEach(request -> {
            logger.info("Request: ID={}, Status={}, Expert={}, Car={}, Report={}",
                request.getId(),
                request.getStatus(),
                request.getExpert() != null ? request.getExpert().getId() : "none",
                request.getCar() != null ? request.getCar().getId() : "none",
                request.getReport() != null ? "present" : "none"
            );
        });
        
        return requests;
    }

    public ExpertiseRequest acceptRequest(Long requestId) {
        ExpertiseRequest request = expertiseRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        System.out.println("Avant: " + request.getStatus());
        request.setStatus(ExpertiseRequest.RequestStatus.ACCEPTED);
        ExpertiseRequest saved = expertiseRequestRepository.save(request);
        System.out.println("Après: " + saved.getStatus());
        return saved;
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

    public void debugUserRequests(Long userId) {
        List<Object[]> rawRequests = expertiseRequestRepository.findRawRequestsByUserId(userId);
        logger.info("Raw database records for user {}: {}", userId, rawRequests.size());
        rawRequests.forEach(row -> {
            logger.info("Raw request data: {}", Arrays.toString(row));
        });
    }
} 