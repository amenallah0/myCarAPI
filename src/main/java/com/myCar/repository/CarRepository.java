package com.myCar.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.myCar.domain.Car;

public interface CarRepository extends JpaRepository<Car, Long> {
    Page<Car> findAll(Pageable pageable);

}
