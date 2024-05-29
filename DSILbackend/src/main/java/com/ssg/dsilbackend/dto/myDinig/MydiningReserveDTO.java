package com.ssg.dsilbackend.dto.myDinig;

import com.ssg.dsilbackend.domain.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MydiningReserveDTO {
    private Long restaurantId;  // Restaurant의 ID 필드 추가
    private String name;
    private String reservationState;
    private String reservationTime;
    private int peopleCount;
    private Long deposit;
    private double averageReviewScore;
    private long reviewCount;  // 리뷰 수 필드
    private Long reservationId;
    private String restaurantImg;
    private boolean isReview; // Indicates if a review exists

    public static MydiningReserveDTO from(Reservation entity, Double averageReviewScore, long reviewCount, boolean isReview) {
        return MydiningReserveDTO.builder()
                .reservationId(entity.getId())
                .restaurantId(entity.getRestaurant().getId())  // Restaurant ID 설정
                .name(entity.getRestaurant().getName())
                .deposit(entity.getRestaurant().getDeposit())
                .reservationState(entity.getReservationStateName().name())
                .reservationTime(entity.getReservationTime().name())
                .peopleCount(entity.getPeopleCount())
                .averageReviewScore(Math.round(averageReviewScore * 10.0) / 10.0)
                .reviewCount(reviewCount)
                .restaurantImg(entity.getRestaurant().getImg())
                .isReview(isReview) // Set the review status
                .build();
    }
}
