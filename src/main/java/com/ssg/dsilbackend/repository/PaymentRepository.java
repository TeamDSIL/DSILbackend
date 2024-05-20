package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Payment;
import com.ssg.dsilbackend.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findByReservation(Reservation reservation);
}
