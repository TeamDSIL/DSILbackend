package com.ssg.dsilbackend.service;
import com.ssg.dsilbackend.domain.Restaurant;
import com.ssg.dsilbackend.dto.CategoryName;
import com.ssg.dsilbackend.dto.main.MainDTO;
import com.ssg.dsilbackend.dto.main.MainMapDTO;
import com.ssg.dsilbackend.dto.main.ToptenDTO;

import java.util.List;


public interface MainService {
    List<MainDTO> findByCategoryName(CategoryName categoryName);

    //카카오 맵
    List<MainMapDTO> getNearbyRestaurants();

    //조회
    List<ToptenDTO> getTopViews();

    //예약
    List<ToptenDTO> getTopReservations();

    //즐겨찾기
    List<ToptenDTO> getTopBookmarks();
}