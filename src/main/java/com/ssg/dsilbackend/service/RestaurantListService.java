package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.dto.CategoryName;
import com.ssg.dsilbackend.dto.restaurantList.RestaurantDetailDTO;
import com.ssg.dsilbackend.dto.restaurantList.RestaurantListDTO;
import com.ssg.dsilbackend.dto.restaurantList.RestaurantReviewDTO;

import java.util.List;


public interface RestaurantListService {
    List<RestaurantDetailDTO> findMenus(Long id); // 메뉴 리스트 조회
//    List<MenuListDTO> findRestaurantDetail (Long id);

    List<RestaurantReviewDTO> getRestaurntReviewsListById(Long restaurantId);
    //    List<RestaurantListDTO> findByCategoryName(CategoryName categoryName);
    List<RestaurantListDTO> findByCategoryNames(List<CategoryName> categoryNames);


    void incrementViewCount(Long id);  // 뷰 카운트 증가

}
