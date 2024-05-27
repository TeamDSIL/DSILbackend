package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.domain.Review;
import com.ssg.dsilbackend.dto.myDinig.*;

import java.util.List;

public interface MyDiningService {
    List<MydiningReserveDTO> getMydiningReserveListById(Long id);

    void registerReview(ReviewRequest reviewRequest);

    boolean cancelReservation(Long reservationId, ReservationUpdateRequest reservationUpdateRequest);

    List<MydiningBookmarkDTO> getMydiningBookmarksListById(Long id);

    boolean removeBookmark(Long bookmarkId);

    List<MydiningReviewsDTO> getMydiningReviewsListById(Long id);

    boolean removeRequestReview(Long reviewId);

}
