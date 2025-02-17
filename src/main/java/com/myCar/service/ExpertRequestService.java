package com.myCar.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.myCar.domain.ExpertRequest;
import com.myCar.domain.User;
import com.myCar.domain.Role;
import com.myCar.dto.ExpertRequestDTO;
import com.myCar.exception.ResourceNotFoundException;
import com.myCar.repository.ExpertRequestRepository;
import com.myCar.repository.UserRepository;

@Service
public class ExpertRequestService {

    @Autowired
    private ExpertRequestRepository expertRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileUploadService fileUploadService;

    public ExpertRequest createRequest(Long userId, String specialization, 
                                     String experience, String currentPosition, 
                                     MultipartFile diploma) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String diplomaUrl = fileUploadService.storeFile(diploma);

        ExpertRequest request = new ExpertRequest();
        request.setUser(user);
        request.setSpecialization(specialization);
        request.setExperience(experience);
        request.setCurrentPosition(currentPosition);
        request.setDiplomaUrl(diplomaUrl);
        request.setStatus(ExpertRequest.RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        return expertRequestRepository.save(request);
    }

    @Transactional
    public ExpertRequest approveRequest(Long requestId) {
        ExpertRequest request = expertRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Expert request not found"));

        if (request.getStatus() != ExpertRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("Cette demande a déjà été traitée");
        }

        request.setStatus(ExpertRequest.RequestStatus.APPROVED);

        User user = request.getUser();
        user.setRole(Role.EXPERT);
        userRepository.save(user);

        return expertRequestRepository.save(request);
    }

    @Transactional
    public ExpertRequest rejectRequest(Long requestId) {
        ExpertRequest request = expertRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Expert request not found"));

        if (request.getStatus() != ExpertRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("Cette demande a déjà été traitée");
        }

        request.setStatus(ExpertRequest.RequestStatus.REJECTED);
        return expertRequestRepository.save(request);
    }

    public List<ExpertRequestDTO> getAllRequests() {
        return expertRequestRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private ExpertRequestDTO convertToDTO(ExpertRequest request) {
        ExpertRequestDTO dto = new ExpertRequestDTO();
        dto.setId(request.getId());
        
        if (request.getUser() != null) {
            dto.setUserId(request.getUser().getId());
            dto.setUsername(request.getUser().getUsername());
            dto.setEmail(request.getUser().getEmail());
        }
        
        dto.setSpecialization(request.getSpecialization());
        dto.setExperience(request.getExperience());
        dto.setCurrentPosition(request.getCurrentPosition());
        dto.setDiplomaUrl(request.getDiplomaUrl());
        dto.setStatus(request.getStatus());
        
        if (request.getCreatedAt() != null) {
            dto.setCreatedAt(request.getCreatedAt().toString());
        } else {
            dto.setCreatedAt("N/A");
        }
        
        return dto;
    }

    public boolean hasPendingRequest(Long userId) {
        return expertRequestRepository.findByUserId(userId).stream()
            .anyMatch(request -> request.getStatus() == ExpertRequest.RequestStatus.PENDING);
    }
} 