package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReserveRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByRestaurantId(Long restaurantId);
    @Query("SELECT r FROM Reservation r WHERE r.reservationDate BETWEEN :startDate AND :endDate")
    List<Reservation> findReservationsByDateRange(LocalDate startDate, LocalDate endDate);
}
