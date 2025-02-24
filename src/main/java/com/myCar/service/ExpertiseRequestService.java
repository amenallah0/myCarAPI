package com.myCar.service;

import com.myCar.domain.ExpertiseRequest;
import com.myCar.domain.User;
import com.myCar.domain.Car;
import com.myCar.repository.ExpertiseRequestRepository;
import com.myCar.repository.UserRepository;
import com.myCar.repository.CarRepository;
import com.myCar.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
} 