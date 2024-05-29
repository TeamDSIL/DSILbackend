package com.ssg.dsilbackend.dto.restaurantList;

import com.ssg.dsilbackend.domain.Menu;
import com.ssg.dsilbackend.dto.CategoryName;
import com.ssg.dsilbackend.dto.Crowd;
import com.ssg.dsilbackend.dto.FacilityName;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantListDTO {
     private Long restaurant_id;
    private String restaurant_name;
    private String restaurant_address;
    private String restaurant_tel;
    private Crowd restaurant_crowd;
    private String restaurant_img;
    private Menu menu_img;
    private Long restaurant_deposit;
    private Long restaurant_table_count;
    private CategoryName categoryName;
    private List<CategoryName> categoryNames;// 여러개의 카테고리가 담긴 카테고리 리스트
    private List<FacilityName> facilityNames;
    private int matchCount; // 검색 결과 조건 갯수를 나타냄(조건에 충족하는 수가 많을 수록 상위에 노출
    private Double score;// 식당별 평점
}

