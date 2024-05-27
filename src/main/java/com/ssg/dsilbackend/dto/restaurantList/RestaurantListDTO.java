package com.ssg.dsilbackend.dto.restaurantList;

import com.ssg.dsilbackend.dto.CategoryName;
import com.ssg.dsilbackend.dto.Crowd;
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
    private Long restaurant_deposit;
    private Long restaurant_table_count;
    private CategoryName categoryName;
    private List<CategoryName> categories;
    private List<CategoryName> categoryNames;// 여러개의 카테고리가 담긴 카테고리 리스트
//    private SerchKeyword? // 검색어로 조회할 수 있게 키워드 검색 구현 예정
}

