package com.myCar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.myCar.domain.ExpertReport;

public interface ExpertReportRepository extends JpaRepository<ExpertReport, Long> {
} 