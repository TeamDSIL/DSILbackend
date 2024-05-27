package com.ssg.dsilbackend.dto.myDinig;

import com.ssg.dsilbackend.domain.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MydiningBookmarkDTO {
    private Long restaurantId;
    private String name;
    private double averageReviewScore;
    private long reviewCount;  // 리뷰 수 필드
    private String address;
    private String tel;
    private Long bookmarkId;
    private String restaurantImg;

    public static MydiningBookmarkDTO from(Bookmark bookmark, Double averageReviewScore, long reviewCount){
        return MydiningBookmarkDTO.builder()
                .bookmarkId(bookmark.getId())
                .restaurantId(bookmark.getRestaurant().getId())
                .name(bookmark.getRestaurant().getName())
                .averageReviewScore(Math.round(averageReviewScore * 10.0) / 10.0)
                .reviewCount(reviewCount)
                .address(bookmark.getRestaurant().getAddress())
                .tel(bookmark.getRestaurant().getTel())
                .restaurantImg(bookmark.getRestaurant().getImg())
                .build();
    }


}
