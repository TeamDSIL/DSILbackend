package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Reservation;
import com.ssg.dsilbackend.dto.ReservationStateName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReserveRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByRestaurantId(Long restaurantId);
    @Query("SELECT r FROM Reservation r WHERE r.reservationDate BETWEEN :startDate AND :endDate")
    List<Reservation> findReservationsByDateRange(LocalDate startDate, LocalDate endDate);

    //예약생태를 기준으로 예약목록을 찾는 쿼리
    List<Reservation> findByRestaurantIdAndReservationStateName(Long restaurantId, ReservationStateName reservationStateName);


}
