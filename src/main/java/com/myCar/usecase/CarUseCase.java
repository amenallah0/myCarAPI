package com.myCar.usecase;

import java.util.List;

import com.myCar.domain.Car;


public interface CarUseCase {
    List<Car> getAllCars();
    Car getCarById(Long id);
    Car saveCar(Car car);
    void deleteCar(Long id);
}
