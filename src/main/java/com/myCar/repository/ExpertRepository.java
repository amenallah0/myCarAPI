package com.myCar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myCar.domain.Expert;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpertRepository extends JpaRepository<Expert, Long> {
    List<Expert> findBySpecialite(String specialite);
    Optional<Expert> findByEmail(String email);
} 