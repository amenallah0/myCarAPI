package com.myCar.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.myCar.domain.ExpertRequest;
import com.myCar.domain.User;
import com.myCar.dto.ExpertRequestDTO;
import com.myCar.exception.ResourceNotFoundException;
import com.myCar.repository.ExpertRequestRepository;  // Modifiez cet import
import com.myCar.repository.UserRepository;
import com.myCar.domain.Role;

@Service
@Transactional
public class ExpertRequestService {

    @Autowired
    private ExpertRequestRepository expertRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public ExpertRequest createRequest(Long userId, String specialization, 
                                     String experience, String currentPosition, 
                                     MultipartFile diploma) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String diplomaUrl = fileStorageService.storeFile(diploma);

        ExpertRequest request = new ExpertRequest();
        request.setUser(user);
        request.setSpecialization(specialization);
        request.setExperience(experience);
        request.setCurrentPosition(currentPosition);
        request.setDiplomaUrl(diplomaUrl);
        request.setStatus(ExpertRequest.RequestStatus.PENDING);

        return expertRequestRepository.save(request);
    }

    @Transactional
    public ExpertRequest approveRequest(Long requestId) {
        ExpertRequest request = expertRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        
        // Mettre à jour le statut de la demande
        request.setStatus(ExpertRequest.RequestStatus.APPROVED);
        
        // Mettre à jour le rôle de l'utilisateur
        User user = request.getUser();
        user.setRole(Role.EXPERT);
        userRepository.save(user);
        
        return expertRequestRepository.save(request);
    }

    public ExpertRequest rejectRequest(Long requestId) {
        ExpertRequest request = expertRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        request.setStatus(ExpertRequest.RequestStatus.REJECTED);
        return expertRequestRepository.save(request);
    }

    public List<ExpertRequestDTO> getAllRequests() {
        return expertRequestRepository.findAll().stream()
            .map(request -> {
                ExpertRequestDTO dto = new ExpertRequestDTO();
                User user = request.getUser();
                
                dto.setId(request.getId());
                dto.setUserId(user.getId());
                dto.setUsername(user.getUsername());
                dto.setEmail(user.getEmail());
                dto.setSpecialization(request.getSpecialization());
                dto.setExperience(request.getExperience());
                dto.setCurrentPosition(request.getCurrentPosition());
                dto.setDiplomaUrl(request.getDiplomaUrl());
                dto.setStatus(request.getStatus());
                dto.setCreatedAt(request.getCreatedAt() != null ? 
                    request.getCreatedAt().toString() : null);
                
                return dto;
            })
            .collect(Collectors.toList());
    }
} 