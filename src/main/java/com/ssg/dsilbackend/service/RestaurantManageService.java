package com.ssg.dsilbackend.service;


import com.ssg.dsilbackend.domain.*;
import com.ssg.dsilbackend.dto.Crowd;
import com.ssg.dsilbackend.dto.reserve.ReserveDTO;
import com.ssg.dsilbackend.dto.restaurantManage.ReplyDTO;
import com.ssg.dsilbackend.dto.restaurantManage.RestaurantManageDTO;
import com.ssg.dsilbackend.dto.restaurantManage.ReviewDTO;


import java.util.List;

public interface RestaurantManageService {
    RestaurantManageDTO getRestaurant(Long id);
    List<RestaurantManageDTO> getRestaurantList(Long memberId);
    RestaurantManageDTO updateRestaurant(Long id, RestaurantManageDTO restaurant);

    RestaurantManageDTO updateCrowd(Long id, Crowd crowd) throws Exception;

    List<ReserveDTO> getReservationList(Long restaurantId);
    ReviewDTO getReview(Reservation reservation);

    ReplyDTO createReply(Long reviewId, String content);

}
