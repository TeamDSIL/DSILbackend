package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.dto.restaurantList.MenuListDTO;

import java.util.List;


public interface RestaurantListService {
    List<MenuListDTO> findMenus(Long id); // 메뉴 리스트 조회
//    List<MenuListDTO> findRestaurantDetail (Long id);
}
