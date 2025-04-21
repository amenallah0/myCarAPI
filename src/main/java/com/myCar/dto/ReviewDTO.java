package com.myCar.dto;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long id;
    private Long carId;
    private Long userId;
    private String userName;
    private String comment;
    private int rating;
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "ReviewDTO{" +
            "id=" + id +
            ", carId=" + carId +
            ", userId=" + userId +
            ", userName='" + userName + '\'' +
            ", rating=" + rating +
            ", comment='" + comment + '\'' +
            ", createdAt=" + createdAt +
            '}';
    }
}