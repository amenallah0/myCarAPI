package com.myCar.service;

import com.myCar.domain.Car;
import com.myCar.domain.Image;
import com.myCar.repository.CarRepository;
import com.myCar.repository.ImageRepository;
import com.myCar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final FileUploadService fileUploadService;

    @Autowired
    public CarService(CarRepository carRepository, UserRepository userRepository, ImageRepository imageRepository, FileUploadService fileUploadService) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.fileUploadService = fileUploadService;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id).orElse(null);
    }

    public Car addCarToUser(Long userId, Car car) {
        car.setUser(userRepository.findById(userId).orElse(null));
        return carRepository.save(car);
    }

    public Car addCarWithImages(Long userId, Car car, List<MultipartFile> files) throws IOException {
        car.setUser(userRepository.findById(userId).orElse(null));
        Car savedCar = carRepository.save(car);
        saveImages(files, savedCar);
        return savedCar;
    }

    private void saveImages(List<MultipartFile> files, Car car) throws IOException {
        for (MultipartFile file : files) {
            String filename = fileUploadService.storeFile(file);
            Image image = new Image();
            image.setFilename(filename);
            image.setCar(car);
            imageRepository.save(image);
        }
    }

    public Car addImagesToCar(Long carId, List<MultipartFile> files) throws IOException {
        Car car = carRepository.findById(carId).orElse(null);
        if (car != null) {
            saveImages(files, car);
        }
        return car;
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }
    
    public List<Car> getLatestCars(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Order.desc("createdAt")));
        return carRepository.findAll(pageable).getContent();
    }



}
