package com.myCar.controller;


import com.myCar.controller.ExpertiseRequestController.ErrorResponse;
import com.myCar.domain.Review;
import com.myCar.dto.ReviewDTO;
import com.myCar.exception.ResourceNotFoundException;
import com.myCar.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/car/{carId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByCarId(@PathVariable Long carId) {
        return ResponseEntity.ok(reviewService.getReviewsByCarId(carId));
    }

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody ReviewDTO reviewDTO, Authentication authentication) {
        try {
            // Logs de débogage
            System.out.println("Tentative d'ajout d'une review");
            System.out.println("Authentication: " + authentication);
            System.out.println("Authorities: " + authentication.getAuthorities());
            System.out.println("Review DTO: " + reviewDTO);

            // Validation
            if (reviewDTO.getCarId() == null) {
                return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", "Car ID is required"));
            }
            
            if (reviewDTO.getUserId() == null) {
                return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", "User ID is required"));
            }
            
            if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
                return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", "Rating must be between 1 and 5"));
            }
            
            if (reviewDTO.getComment() == null || reviewDTO.getComment().trim().isEmpty()) {
                return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", "Comment is required"));
            }

            Review review = reviewService.addReview(reviewDTO);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout de la review: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
        }
    }
}