package com.myCar.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "expertise_request")
public class ExpertiseRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"cars", "password", "createdAt", "updatedAt"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "expert_id")
    @JsonIgnoreProperties({"cars", "password", "createdAt", "updatedAt"})
    private User expert;

    @ManyToOne
    @JoinColumn(name = "car_id")
    @JsonIgnoreProperties({"user", "images"})
    private Car car;

    private String message;
    private LocalDateTime requestDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status = RequestStatus.PENDING;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "expertiseRequest")
    @JsonIgnoreProperties("expertiseRequest")
    private ExpertReport report;

    public enum RequestStatus {
        PENDING,    // En attente
        ACCEPTED,   // Acceptée
        REJECTED,   // Rejetée
        COMPLETED   // Rapport soumis
    }

    @PrePersist
    protected void onCreate() {
        requestDate = LocalDateTime.now();
    }
} 