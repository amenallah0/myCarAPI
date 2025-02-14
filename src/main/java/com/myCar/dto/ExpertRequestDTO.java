package com.myCar.dto;

import com.myCar.domain.ExpertRequest.RequestStatus;

import lombok.Data;

@Data
public class ExpertRequestDTO {
    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String specialization;
    private String experience;
    private String currentPosition;
    private String diplomaUrl;
    private RequestStatus status;
    private String createdAt;
}