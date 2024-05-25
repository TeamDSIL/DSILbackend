package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.domain.Reservation;


import com.ssg.dsilbackend.domain.Restaurant;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 해당 리포티토리는 예약 테이블을 사용하는 개발자들이 공통으로 정의해놓은 곳이다.
 * reservation state와 관련된 코드는 스케줄러 클래스와 연동
 * 작성자 : [Imhwan]
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByMembers(Members member);

    //벌크 연산 사용으로 인한 쿼리 실행 (delete,update,insert 문만 사용가능)
    @Modifying(clearAutomatically = true) //JPA의 1차 캐시 값과 다르면 충돌 가능성 존재하여 clearAutomatically 설정
    //JPQL을 사용하여 예약 상태가 취소인 예약건들의 리뷰 삭제
    @Query("DELETE FROM Review WHERE reservation IN (SELECT r FROM Reservation r WHERE r.reservationStateName = 'CANCELED')")
    void deleteExpiredReservationsAndAssociatedReviews();

    @Modifying(clearAutomatically = true)
    //예약 상태가 취소된지 30일이 지난 데이터들 삭제 메서드
    @Query("DELETE FROM Reservation r WHERE r.reservationStateName = 'CANCELED' AND r.createdTime <= :ExpiredDate")
    void deleteExpiredReservation(@Param("ExpiredDate") LocalDateTime ExpiredDate);

    //reserved 상태의 데이터들 현재 날짜와 비교하여 지났을 경우 자동으로 완료 상태로 변경)
    @Query("SELECT r FROM Reservation r WHERE r.reservationDate < :now AND r.reservationStateName = 'RESERVED'")
    List<Reservation> updateReservationStatusToCompleted(@Param("now") LocalDate now,Pageable pageable);

    // 예약 수가 많은 상위 10개 식당 조회
    @Query("SELECT r.restaurant FROM Reservation r " +
            "GROUP BY r.restaurant.id ORDER BY COUNT(r.id) DESC")
    Page<Restaurant> findTopByReservations(Pageable pageable);

}

