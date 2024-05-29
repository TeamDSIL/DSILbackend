package com.ssg.dsilbackend.controller;

import com.ssg.dsilbackend.dto.CategoryName;
import com.ssg.dsilbackend.dto.FacilityName;
import com.ssg.dsilbackend.dto.restaurantList.RestaurantBookmarkDTO;
import com.ssg.dsilbackend.dto.restaurantList.RestaurantDetailDTO;
import com.ssg.dsilbackend.dto.restaurantList.RestaurantListDTO;
import com.ssg.dsilbackend.dto.restaurantList.RestaurantReviewDTO;
import com.ssg.dsilbackend.service.RestaurantListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant")
public class RestaurantListController {
    @Autowired
    private RestaurantListService restaurantListService;



    @GetMapping("/detail/{id}") // 식당아이디 기준으로 식당 상세정보 불러오기
    public ResponseEntity<List<RestaurantDetailDTO>> getMenulist(@PathVariable Long id){
        List<RestaurantDetailDTO> menulist = restaurantListService.findMenus(id);
        return ResponseEntity.ok(menulist);
    }
    @GetMapping("/detail/review/{id}") // 식당 아이디를 기준으로 리뷰 데이터 불러오기
    public ResponseEntity<List<RestaurantReviewDTO>> getrestaurantlist(@PathVariable Long id){
        List<RestaurantReviewDTO> reviewlist = restaurantListService.getRestaurntReviewsListById(id);
        return ResponseEntity.ok(reviewlist);
    }
    @GetMapping("/list")
    public ResponseEntity<List<RestaurantListDTO>> getRestaurantsByCategory(
            @RequestParam(required = false) List<CategoryName> categoryNames,
            @RequestParam(required = false) List<FacilityName> facilityNames,
            @RequestParam(required = false) String search
    ) {
        List<RestaurantListDTO> restaurants = restaurantListService.findByFilters(categoryNames, facilityNames, search);
        return ResponseEntity.ok(restaurants); // 정상적으로 데이터가 있으면 200 OK와 함께 데이터 반환
    }
    @PostMapping("/bookmark")
    public ResponseEntity<?> addBookmark(@RequestBody RestaurantBookmarkDTO bookmarkDTO) {
        try {
            restaurantListService.addBookmark(bookmarkDTO.getMember_id(),bookmarkDTO.getRestaurant_id());
            return ResponseEntity.ok("북마크 생성 성공");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/bookmark")
    public ResponseEntity<String> deleteBookmark(@RequestBody RestaurantBookmarkDTO bookmarkDTO) {
        try {
            restaurantListService.removeBookmark(bookmarkDTO);
            return ResponseEntity.ok("북마크 삭제 성공");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        }
    }
