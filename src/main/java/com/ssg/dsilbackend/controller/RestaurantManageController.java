package com.ssg.dsilbackend.controller;

import com.ssg.dsilbackend.domain.Reply;
import com.ssg.dsilbackend.domain.Reservation;
import com.ssg.dsilbackend.domain.Review;
import com.ssg.dsilbackend.dto.AvailableTimeTable;
import com.ssg.dsilbackend.dto.Crowd;
import com.ssg.dsilbackend.dto.reserve.ReserveDTO;
import com.ssg.dsilbackend.dto.restaurantManage.AvailableTimeDTO;
import com.ssg.dsilbackend.dto.restaurantManage.ReplyDTO;
import com.ssg.dsilbackend.dto.restaurantManage.RestaurantManageDTO;
import com.ssg.dsilbackend.dto.restaurantManage.ReviewDTO;
import com.ssg.dsilbackend.service.RestaurantManageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ssg.dsilbackend.domain.Restaurant;

import java.util.List;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
@Log4j2
public class RestaurantManageController {
    private final RestaurantManageService restaurantManageService;


    //식당id로 하나의 식당을 조회하는 메소드
    @GetMapping("/{restaurant-id}")
    public ResponseEntity<RestaurantManageDTO> getRestaurant(@PathVariable("restaurant-id") Long restaurantId) {
        RestaurantManageDTO restaurantDTO = restaurantManageService.getRestaurant(restaurantId);
        if (restaurantDTO == null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(restaurantDTO);
    }

    //식당정보를 수정하는 메소드
    @PutMapping("/{restaurant-id}")
    public ResponseEntity<RestaurantManageDTO> updateRestaurant(@PathVariable Long id, @RequestBody RestaurantManageDTO restaurantDTO) {
        RestaurantManageDTO updatedRestaurant = restaurantManageService.updateRestaurant(id, restaurantDTO);
        return ResponseEntity.ok(updatedRestaurant);
    }

    //멤버id로 해당하는 식당 리스트를 조회하는 메소드
    @GetMapping("/{memberId}/restaurants")
    public ResponseEntity<List<RestaurantManageDTO>> getRestaurantsByMember(@PathVariable Long memberId) {
        List<RestaurantManageDTO> restaurants = restaurantManageService.getRestaurantList(memberId);
        if (restaurants.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(restaurants);
    }

    //식당 혼잡도를 변경하는 메소드
    @PatchMapping("/{restaurantId}/crowd")
    public ResponseEntity<RestaurantManageDTO> updateCrowd(@PathVariable("restaurantId") Long restaurantId, @RequestParam("status") Crowd crowd) {
        try {
            RestaurantManageDTO updatedRestaurantDTO = restaurantManageService.updateCrowd(restaurantId, crowd);
            System.out.println(updatedRestaurantDTO);
            return ResponseEntity.ok(updatedRestaurantDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //식당id에 해당하는 예약목록을 리턴하는 메소드
    @GetMapping("/{restaurant-id}/reservations")
    public ResponseEntity<List<ReserveDTO>> getReservationList(@PathVariable("restaurant-id") Long restaurantId) {
        List<ReserveDTO> reservations = restaurantManageService.getReservationList(restaurantId);
        if (reservations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reservations);
    }
    //식당에 해당하는 리뷰 목록을 불러오는 메소드
    @GetMapping("/{restaurant-id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewList(@PathVariable("restaurant-id") Long restaurantId) {
        try {
            log.info("리뷰 뽑는 restaurant ID: " + restaurantId);
            List<ReviewDTO> reviews = restaurantManageService.getReviewList(restaurantId);
//            log.info("찾은 리뷰 목록: "+reviews);
            if (reviews.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            log.error("Error while fetching review list for restaurant ID: {}", restaurantId, e);
            return ResponseEntity.status(500).body(null);
        }
    }

    //예약에 해당하는 review를 찾는 메소드
//    @GetMapping("/{reservation-id}/review")
//    public ResponseEntity<ReviewDTO> getReview(@PathVariable Reservation reservation) {
//        ReviewDTO reviewDTO = restaurantManageService.getReview(reservation);
//        if (reviewDTO.isDeleteStatus()) {
//            return ResponseEntity.noContent().build();
//        }
//        return  ResponseEntity.ok(reviewDTO);
//    }

    //해당 리뷰에 답글을 작성하는 메소드
    @GetMapping("/reviews/{review-id}")
    public ResponseEntity<ReplyDTO> createReply(@PathVariable("review-id") Long reviewId, String content){
        ReplyDTO replyDTO = restaurantManageService.createReply(reviewId, content);
        return ResponseEntity.ok(replyDTO);
    }

    // 새로운 AvailableTime 인스턴스를 생성하고 해당 예약가능시간DTO를 리턴하는 메소드
    @PostMapping("/{restaurantId}/available-times")
    public ResponseEntity<AvailableTimeDTO> createAvailableTime(@PathVariable Long restaurantId, @RequestParam AvailableTimeTable slot) {
        AvailableTimeDTO newAvailableTimeDTO = restaurantManageService.createAvailableTime(restaurantId, slot);
        return ResponseEntity.ok(newAvailableTimeDTO);
    }

    // 지정된 AvailableTime 인스턴스를 삭제하는 메소드
    @DeleteMapping("/{restaurantId}/available-times")
    public ResponseEntity<Void> deleteAvailableTime(@PathVariable Long restaurantId, @RequestParam AvailableTimeTable slot) {
        restaurantManageService.deleteAvailableTime(restaurantId, slot);
        return ResponseEntity.ok().build();
    }


}
