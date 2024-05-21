package com.ssg.dsilbackend.dto.userManage;

import com.ssg.dsilbackend.domain.Category;
import com.ssg.dsilbackend.domain.Menu;
import com.ssg.dsilbackend.domain.Permission;
import com.ssg.dsilbackend.dto.CategoryName;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RestaurantRegisterDTO {

    private String email; // 멤버
    private String password; // 멤버
    private String userName; // 멤버
    private String userTel; // 멤버
    private String userAddress; // 멤버
    private String restaurantName; // 식당
    private String registerNumber; // 식당
    private String restaurantTel; //식당
    private String restaurantAddress; //식당
    private String postcode; // 식당
    private Long tableCount; //식당
    private Long deposit; // 식당
    private MultipartFile img; // 식당
    private String imgUrl;
    private String description; // 식당
    private String[] categories; // 식당 // 카테고리
    private List<RegisterMenuDTO> menuDTOs; // 식당 // 메뉴
    private String[] facilities; // 식당 // 편의시설

}
