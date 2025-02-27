package com.myCar.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import javax.persistence.Lob;
import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.PrePersist;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpertReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String criticalData;
    private LocalDate expertiseDate;
    private String message;
    private String expertName;
    private String expertEmail;
    private String expertPhone;
    private LocalDateTime submissionDate;
    
    // Nouveau champ pour le fichier
    private String fileName;
    private String fileType;
    
    @Lob
    private byte[] fileData;

    @PrePersist
    protected void onCreate() {
        this.submissionDate = LocalDateTime.now();
    }
}
