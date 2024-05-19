package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.domain.Reservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByMembers(Members member);

    //벌크 연산 사용으로 인한 쿼리 실행
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Review WHERE reservation IN (SELECT r FROM Reservation r WHERE r.reservationStateName = 'CANCELED')")
    void deleteExpiredReservationsAndAssociatedReviews();

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Reservation r WHERE r.reservationStateName = 'CANCELED' AND r.createdTime <= :ExpiredDate")
    void deleteExpiredReservation(@Param("ExpiredDate") LocalDateTime ExpiredDate);

    @Query("SELECT r FROM Reservation r WHERE r.reservationDate < :now AND r.reservationStateName = 'RESERVED'")
    List<Reservation> updateReservationStatusToCompleted(@Param("now") LocalDate now,Pageable pageable);
}

