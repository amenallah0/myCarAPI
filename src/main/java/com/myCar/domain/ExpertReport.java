package com.myCar.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import javax.persistence.Lob;
import java.time.LocalDate;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.PrePersist;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "expert_report", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"expertise_request_id"})
})
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"expertReports", "cars", "password", "createdAt", "updatedAt"})
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expertise_request_id", unique = true)
    @JsonIgnoreProperties({"report", "user", "expert", "car"})
    private ExpertiseRequest expertiseRequest;

    @PrePersist
    protected void onCreate() {
        this.submissionDate = LocalDateTime.now();
    }
}
