package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 해당 리포지토리는 PaymentService, ReserveService에서 사용하기 위한 코드로
 * cancelReservation (예약 취소) refundPayment(환불) 메서드에서 사용된다.
 * 작성자 : [Imhwan]
 */
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findByReservationId(Long reservationId);
}
