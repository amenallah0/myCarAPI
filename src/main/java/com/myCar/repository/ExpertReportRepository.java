package com.myCar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.myCar.domain.ExpertReport;
import java.util.List;

@Repository
public interface ExpertReportRepository extends JpaRepository<ExpertReport, Long> {
    List<ExpertReport> findByExpertiseRequestId(Long expertiseRequestId);

    @Modifying
    @Query("DELETE FROM ExpertReport e WHERE e.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
} 