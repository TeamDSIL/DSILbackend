package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.domain.Payment;
import com.ssg.dsilbackend.domain.Point;
import com.ssg.dsilbackend.domain.Reservation;
import com.ssg.dsilbackend.dto.PaymentStatus;
import com.ssg.dsilbackend.dto.payment.PaymentDTO;
import com.ssg.dsilbackend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final RefundService refundService;
    private final PointManageRepository pointManageRepository;

    public void savePayment(PaymentDTO paymentDTO, Long reservationId) {
        try {

            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new EntityNotFoundException("reservationId not found with ID: " + reservationId));

            Members members = reservation.getMembers();

            String email = members.getEmail();
            String tel = members.getTel();
            String name = members.getName();

            Point point = members.getPoint();

            Long pointUsage = paymentDTO.getPointUsage();

            if (point.getCurrentPoint() >= pointUsage) {
                point.setCurrentPoint(point.getCurrentPoint() - pointUsage); // 사용한 포인트 차감
                pointManageRepository.save(point); // 포인트 정보 저장

                Payment payment = Payment.builder()
                        .amount(paymentDTO.getAmount())
                        .paymentTime(LocalDateTime.now())
                        .pg(paymentDTO.getPg())
                        .buyerEmail(email)
                        .buyerName(name)
                        .buyerTel(tel)
                        .payMethod(paymentDTO.getPay_method())
                        .paymentTime(LocalDateTime.now())
                        .name(paymentDTO.getName())
                        .merchantUid(paymentDTO.getMerchant_uid())
                        .reservation(reservation)
                        .paymentStatus(PaymentStatus.COMPLETED)
                        .pointUsage(paymentDTO.getPointUsage())
                        .impUid(paymentDTO.getImpUid())
                        .build();

                paymentRepository.save(payment);

            } else {
                throw new RuntimeException("포인트 부족 무튼 오류임");
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    @SneakyThrows
    public void refundPayment(Long reservationId) {
        Payment payment = paymentRepository.findByReservationId(reservationId).orElseThrow(() -> new EntityNotFoundException("Payment not found with ID: " + reservationId));
        String token = refundService.getToken();
        refundService.cancelPayment(payment.getImpUid(), "예약 취소로 인한 환불", token);
        payment.cancelPaymentStatus(PaymentStatus.CANCELED);
        paymentRepository.save(payment);
    }

}
