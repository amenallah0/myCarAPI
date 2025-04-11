package com.myCar.repository;

import com.myCar.domain.ExpertiseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpertiseRequestRepository extends JpaRepository<ExpertiseRequest, Long> {
    @Query("SELECT DISTINCT er FROM ExpertiseRequest er " +
           "LEFT JOIN FETCH er.expert e " +
           "LEFT JOIN FETCH er.user u " +
           "LEFT JOIN FETCH er.car c " +
           "LEFT JOIN FETCH er.report r " +
           "WHERE er.expert.id = :expertId " +
           "ORDER BY er.requestDate DESC")
    List<ExpertiseRequest> findByExpertId(Long expertId);

    List<ExpertiseRequest> findByUserId(Long userId);
    List<ExpertiseRequest> findByCarId(Long carId);
} 