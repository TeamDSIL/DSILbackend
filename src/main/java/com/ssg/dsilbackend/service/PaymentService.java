package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.domain.Payment;
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
    private final MemberRepository memberRepository;
    private final RefundService refundService;

    public void savePayment(PaymentDTO paymentDTO, Long reservationId) {
        try {
            Long memberId = 44L;

            Members member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));

            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new EntityNotFoundException("reservationId not found with ID: " + reservationId));

            Payment payment = Payment.builder()
                    .amount(paymentDTO.getAmount())
                    .paymentTime(LocalDateTime.now())
                    .pg(paymentDTO.getPg())
                    .buyerEmail(member.getEmail())
                    .buyerName(member.getName())
                    .buyerTel(member.getTel())
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
