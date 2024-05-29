package com.ssg.dsilbackend.dto.restaurantList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantBookmarkDTO {
    private Long memberId;
    private Long restaurantId;
}
