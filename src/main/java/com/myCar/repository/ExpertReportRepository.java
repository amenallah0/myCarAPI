package com.myCar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.myCar.domain.ExpertReport;
import java.util.List;

@Repository
public interface ExpertReportRepository extends JpaRepository<ExpertReport, Long> {
    List<ExpertReport> findByExpertiseRequestId(Long expertiseRequestId);
} 