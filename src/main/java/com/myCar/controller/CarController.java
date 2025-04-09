package com.myCar.controller;

import com.myCar.domain.Car;
import com.myCar.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        Car car = carService.getCarById(id);
        if (car != null) {
            return ResponseEntity.ok(car);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/user/{userId}/add")
    public ResponseEntity<Car> addCarToUser(@PathVariable Long userId, @RequestBody Car car) {
        Car savedCar = carService.addCarToUser(userId, car);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCar);
    }

    @PostMapping("/user/{userId}/addWithImages")
    public ResponseEntity<Car> addCarWithImagesToUser(
            @PathVariable Long userId,
            @RequestPart("car") Car car,
            @RequestPart("files") List<MultipartFile> files) {

        try {
            Car createdCar = carService.addCarWithImages(userId, car, files);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCar);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{carId}/images")
    public ResponseEntity<Car> uploadImagesToCar(@PathVariable Long carId, @RequestParam("files") List<MultipartFile> files) {
        try {
            Car car = carService.addImagesToCar(carId, files);
            return ResponseEntity.ok(car);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/latest")
    public ResponseEntity<List<Car>> getLatestCars() {
        List<Car> cars = carService.getLatestCars(3); // Fetch last 3 cars
        return ResponseEntity.ok(cars);
    }
    
    @PutMapping("/{id}/promote")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Car> updatePromotionStatus(
            @PathVariable Long id,
            @RequestParam boolean promoted) {
        try {
            System.out.println("Updating promotion status for car " + id + " to " + promoted);
            Car updatedCar = carService.updatePromotionStatus(id, promoted);
            return ResponseEntity.ok(updatedCar);
        } catch (Exception e) {
            System.err.println("Error updating promotion status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/promoted")
    public ResponseEntity<List<Car>> getPromotedCars() {
        List<Car> cars = carService.getPromotedCars();
        return ResponseEntity.ok(cars);
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<Car> updateAvailability(@PathVariable Long id, @RequestParam boolean available) {
        try {
            Car updatedCar = carService.updateAvailability(id, available);
            return ResponseEntity.ok(updatedCar);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}