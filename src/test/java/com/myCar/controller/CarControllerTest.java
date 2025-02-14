package com.myCar.controller;

import com.myCar.domain.Car;
import com.myCar.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class CarControllerTest {

    @InjectMocks
    private CarController carController;

    @Mock
    private CarService carService;

    private Car car;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        car = new Car();
        car.setMake("Toyota");
        car.setModel("Camry");
        car.setColor("Red");
        car.setYear(2020);
        car.setPowerRating(300);
        car.setNumberOfDoors(4);
        car.setFuelTankCapacity(50);
        car.setMaximumSpeed(220);
        car.setMileage(10000);
        car.setPrice(30000.00);
    }

    @Test
    public void testGetAllCars_Success() {
        List<Car> carList = new ArrayList<>();
        carList.add(car);

        when(carService.getAllCars()).thenReturn(carList);

        ResponseEntity<List<Car>> response = carController.getAllCars();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(carList, response.getBody());
        verify(carService, times(1)).getAllCars();
    }

    @Test
    public void testGetCarById_Success() {
        when(carService.getCarById(anyLong())).thenReturn(car);

        ResponseEntity<Car> response = carController.getCarById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(car, response.getBody());
        verify(carService, times(1)).getCarById(1L);
    }

    @Test
    public void testGetCarById_NotFound() {
        when(carService.getCarById(anyLong())).thenReturn(null);

        ResponseEntity<Car> response = carController.getCarById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(carService, times(1)).getCarById(1L);
    }

    @Test
    public void testAddCarToUser_Success() {
        when(carService.addCarToUser(anyLong(), any(Car.class))).thenReturn(car);

        ResponseEntity<Car> response = carController.addCarToUser(1L, car);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(car, response.getBody());
        verify(carService, times(1)).addCarToUser(1L, car);
    }

    @Test
    public void testDeleteCar_Success() {
        doNothing().when(carService).deleteCar(anyLong());

        ResponseEntity<Void> response = carController.deleteCar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(carService, times(1)).deleteCar(1L);
    }
}
