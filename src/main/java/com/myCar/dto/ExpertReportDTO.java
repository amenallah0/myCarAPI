package com.myCar.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class ExpertReportDTO {
    private String title;
    private String criticalData;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expertiseDate;
    
    private String message;
    private String expertName;
    private String expertEmail;
    private String expertPhone;
    private MultipartFile file;
} 