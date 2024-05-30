package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Reservation;
import com.ssg.dsilbackend.domain.Review;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {


    Review findByReservation(Reservation reservation);
    long countByReservationRestaurantId(Long restaurantId);
    @Query("SELECT AVG(r.score) FROM Review r WHERE r.reservation.restaurant.id = :restaurantId")
    Double findAverageScoreByRestaurantId(@Param("restaurantId") Long restaurantId);
    // 멤버아이디로 예약 리스트 뽑기
    List<Review> findByReservationMembersId(Long memberId);


    @Query("SELECT r FROM Review r JOIN FETCH r.reservation res WHERE res.restaurant.id = :restaurantId")
    List<Review> findReviewsByRestaurantId(@Param("restaurantId") Long restaurantId);

    List<Review> findByReservationRestaurantId(Long restaurantId);
    boolean existsByReservationId(Long reservationId);
    @Query("SELECT r FROM Review r JOIN FETCH r.reservation res WHERE res.restaurant.id = :restaurantId") //지워버릴지도
    List<Review> findByReservationRestaurantIdWithFetchJoin(@Param("restaurantId") Long restaurantId);

    @Query("SELECT r FROM Review r JOIN FETCH r.reservation res")
    List<Review> findAllWithReservation();

}


