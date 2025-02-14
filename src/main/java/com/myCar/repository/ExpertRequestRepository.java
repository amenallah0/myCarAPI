package com.myCar.repository;

import com.myCar.domain.ExpertRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ExpertRequestRepository extends JpaRepository<ExpertRequest, Long> {
    List<ExpertRequest> findByUserId(Long userId);
} 