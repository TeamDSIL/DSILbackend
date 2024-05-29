package com.ssg.dsilbackend.controller;

import com.ssg.dsilbackend.domain.Restaurant;
import com.ssg.dsilbackend.dto.CategoryName;
import com.ssg.dsilbackend.dto.main.MainDTO;
import com.ssg.dsilbackend.dto.main.MainMapDTO;
import com.ssg.dsilbackend.dto.main.ToptenDTO;
import com.ssg.dsilbackend.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class MainController {


    @Autowired
    private MainService mainService;

//    @GetMapping("restaurants/category/{categoryName}")
//    public ResponseEntity<List<MainDTO>> getRestaurantsByCategory(@PathVariable CategoryName categoryName) {
//        List<MainDTO> restaurants = mainService.findByCategoryName(categoryName);
//        if (restaurants.isEmpty()) {
//            return ResponseEntity.notFound().build(); // 카테고리에 해당하는 식당이 없는 경우 404 Not Found 반환
//        }
//        return ResponseEntity.ok(restaurants); // 정상적으로 데이터가 있으면 200 OK와 함께 데이터 반환
//    }

    // 카카오 맵
    @GetMapping("/api/restaurants")
    public ResponseEntity<List<MainMapDTO>> getNearbyRestaurants(){
        List<MainMapDTO> nearRestaurants = mainService.getNearbyRestaurants();
        return ResponseEntity.ok(nearRestaurants);
    }

    //조회
    @GetMapping("main/topten/views")
    public ResponseEntity<List<ToptenDTO>> getTopViews(){
       List<ToptenDTO> topViews = mainService.getTopViews();
       return ResponseEntity.ok(topViews);
    }

    //예약
    @GetMapping("main/topten/reservations")
    public ResponseEntity<List<ToptenDTO>> getTopReservations(){
        List<ToptenDTO> topReserves = mainService.getTopReservations();
        return ResponseEntity.ok(topReserves);
    }

    //즐겨찾기
    @GetMapping("main/topten/bookmarks")
    public ResponseEntity<List<ToptenDTO>> getTopBookmarks(){
        List<ToptenDTO> topBookmarks = mainService.getTopBookmarks();
        return ResponseEntity.ok(topBookmarks);
    }


}