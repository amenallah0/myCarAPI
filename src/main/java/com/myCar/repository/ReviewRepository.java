package com.myCar.repository;

import com.myCar.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCarIdOrderByCreatedAtDesc(Long carId);
}