package com.myCar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myCar.domain.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    // You can add custom query methods here if needed
}
