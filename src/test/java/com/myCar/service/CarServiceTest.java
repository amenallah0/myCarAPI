package com.myCar.service;

import com.myCar.domain.Car;
import com.myCar.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllCars() {
        List<Car> cars = new ArrayList<>();
        cars.add(generateCar("Toyota", "Camry", 2020));
        cars.add(generateCar("Honda", "Civic", 2019));

        when(carRepository.findAll()).thenReturn(cars);

        List<Car> retrievedCars = carService.getAllCars();

        assertEquals(2, retrievedCars.size());
        verify(carRepository, times(1)).findAll();
    }

    @Test
    public void testGetCarById() {
        Long carId = 1L;
        Car car = generateCar("Toyota", "Camry", 2020);

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));

        Car retrievedCar = carService.getCarById(carId);

        assertEquals(car, retrievedCar);
        verify(carRepository, times(1)).findById(carId);
    }

    @Test
    public void testSaveCar() {
        Car car = generateCar("Toyota", "Camry", 2020);
        Car savedCar = generateCar("Toyota", "Camry", 2020);

        when(carRepository.save(car)).thenReturn(savedCar);

        // Car returnedCar = carService.saveCar(car);

       //  assertEquals(savedCar, returnedCar);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    public void testDeleteCar() {
        Long carId = 1L;

        carService.deleteCar(carId);

        verify(carRepository, times(1)).deleteById(carId);
    }

    // Helper method to generate a Car object with required fields
    private Car generateCar(String make, String model, int year) {
        return Car.builder()
                .make(make)
                .model(model)
                .color("Color")
                .year(year)
                .powerRating(200)
                .numberOfDoors(4)
                .fuelTankCapacity(50)
                .maximumSpeed(150)
                .mileage(10000)
                .price(20000.0)
                .build();
    }
}
