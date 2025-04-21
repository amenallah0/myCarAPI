package com.myCar.service;


import com.myCar.dto.ReviewDTO;
import com.myCar.exception.ResourceNotFoundException;
import com.myCar.domain.Car;
import com.myCar.domain.Review;
import com.myCar.domain.User;
import com.myCar.repository.CarRepository;
import com.myCar.repository.ReviewRepository;
import com.myCar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ReviewDTO> getReviewsByCarId(Long carId) {
        return reviewRepository.findByCarIdOrderByCreatedAtDesc(carId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Review addReview(ReviewDTO reviewDTO) {
        Review review = new Review();
        
        // Récupérer la voiture
        Car car = carRepository.findById(reviewDTO.getCarId())
            .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
            
        // Récupérer l'utilisateur
        User user = userRepository.findById(reviewDTO.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
        review.setCar(car);
        review.setUser(user);
        review.setComment(reviewDTO.getComment());
        review.setRating(reviewDTO.getRating());
        review.setCreatedAt(LocalDateTime.now());
        
        return reviewRepository.save(review);
    }

    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setCarId(review.getCar().getId());
        dto.setUserId(review.getUser().getId());
        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}