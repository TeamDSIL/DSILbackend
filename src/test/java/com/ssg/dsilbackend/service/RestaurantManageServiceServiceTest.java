package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.domain.*;
import com.ssg.dsilbackend.dto.*;
import com.ssg.dsilbackend.dto.reserve.ReserveDTO;
import com.ssg.dsilbackend.dto.restaurantManage.ReservationDTO;
import com.ssg.dsilbackend.dto.restaurantManage.RestaurantManageDTO;
import com.ssg.dsilbackend.dto.restaurantManage.ReviewDTO;
import com.ssg.dsilbackend.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.cors.reactive.PreFlightRequestWebFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class RestaurantManageServiceServiceTest {

    @Autowired
    private RestaurantManageService restaurantManageService;

    @Autowired
    private RestaurantManageRepository restaurantManageRepository;

    @Autowired
    private ReserveRepository reserveRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Test
    void getRestaurantListTest() {
        Long memberId = 15L;
        // Restaurant 조회
        List<RestaurantManageDTO> restaurants = restaurantManageService.getRestaurantList(memberId);
        // 로그 출력
        System.out.println(restaurants.toString());
    }
    @Test
    void getReservationTest(){
        Long restaurantId = 1l;
        List<ReservationDTO> reservations = restaurantManageService.getReservationList(restaurantId);
        System.out.println(reservations);
    }

    @Test
    void getReviewTest() {
        Long restaurantId = 1l;
        List<ReviewDTO> reviews = restaurantManageService.getReviewList(restaurantId);
        System.out.println(reviews);
    }



}
