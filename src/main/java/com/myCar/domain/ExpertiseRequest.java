package com.myCar.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpertiseRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "expert_id")
    private User expert;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    private String message;
    private LocalDateTime requestDate;
    
    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    @OneToOne(cascade = CascadeType.ALL)
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