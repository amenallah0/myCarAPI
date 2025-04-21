package com.myCar.dto;


import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class ReviewDTO {
    private Long id;
    private Long carId;
    private Long userId;
    private String userName;
    private String comment;
    private int rating;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
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