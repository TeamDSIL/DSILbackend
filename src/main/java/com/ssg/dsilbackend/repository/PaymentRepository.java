package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findByReservationId(Long reservationId);
}
