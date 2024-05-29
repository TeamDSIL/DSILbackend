package com.ssg.dsilbackend.dto.restaurantList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantReviewDTO {
 private Long restaurant_id;
    private Long id; // 리뷰 아이디
    private String content;
    private LocalDate registerDate;
    private Double score;
    private String review_img;
    private String name; // 예약테이블 예약자명
    private Double averageScore;
    private Long reviewCount;
    private List<Long> ratingsCount;
}
