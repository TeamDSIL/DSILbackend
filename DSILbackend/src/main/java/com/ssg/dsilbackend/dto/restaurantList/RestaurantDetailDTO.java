package com.ssg.dsilbackend.dto.restaurantList;

import com.ssg.dsilbackend.dto.Crowd;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDetailDTO { // 식당상세페이지 dto
    private Long id; // 메뉴 아이디
    private String name; // 메뉴 이름(피자, 치킨)
    private Long price; // 메뉴 가격
    private String img; // 메뉴 이미지
    private String menu_info; // 메뉴 서브명(콤비네이션, 불고기, 양념, 후라이드)
    // 추가
    private Long restaurant_id;
    private String restaurant_name;
    private String restaurant_description;
    private String restaurant_address;
    private String restaurant_tel;
    private Crowd restaurant_crowd;
    private String restaurant_img;
    private Long restaurant_deposit;
    private Long restaurant_table_count;
    private List<String> facilies;
    private List<String> categories;
}
