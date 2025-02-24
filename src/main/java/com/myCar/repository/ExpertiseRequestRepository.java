package com.myCar.repository;

import com.myCar.domain.ExpertiseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpertiseRequestRepository extends JpaRepository<ExpertiseRequest, Long> {
    List<ExpertiseRequest> findByExpertId(Long expertId);
    List<ExpertiseRequest> findByUserId(Long userId);
    List<ExpertiseRequest> findByCarId(Long carId);
} 