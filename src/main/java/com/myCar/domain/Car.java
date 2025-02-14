package com.myCar.domain;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Make is mandatory")
    private String make;

    @NotBlank(message = "Model is mandatory")
    private String model;

    @NotBlank(message = "Color is mandatory")
    private String color;

    @Min(value = 1886, message = "Year must be later than 1885")
    private int year;

    @Min(value = 0, message = "Power rating must be positive")
    private int powerRating;

    @Min(value = 1, message = "Number of doors must be at least 1")
    private int numberOfDoors;

    @Min(value = 0, message = "Fuel tank capacity must be positive")
    private int fuelTankCapacity;

    @Min(value = 0, message = "Maximum speed must be positive")
    private int maximumSpeed;

    @Min(value = 0, message = "Mileage must be positive")
    private int mileage;

    private String options;

    @Min(value = 0, message = "Price must be positive")
    private double price;

    private LocalDateTime updatedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties("cars") // Ignore user's cars during serialization
    private User user;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("car") // Ignore car reference in images during serialization
    private List<Image> images;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
