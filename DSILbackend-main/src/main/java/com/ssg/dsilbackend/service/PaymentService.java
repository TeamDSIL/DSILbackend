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
/**
 * 해당 클래스는 결제와 관련된 클래스로 결제 정보 저장과 환불 메서드가 구현되어있다.
 * 작성자 : [Imhwan}
 */
@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final PointManageRepository pointManageRepository;
    private final RefundService refundService;

    //해댱 메서드는 결제 정보를 저장하는 것이다
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

    //해당 메서드는 환불 로직으로 환불을 사용하기 위해 Api에서 명세한 토큰을 가져오며 결제 상태를 취소로 변경한다
    @SneakyThrows //롬복에서 제공하는 어노테이션으로 검사된 예외 throw (테스트로 써보는데 아직 문제 없는듯...(사용 유의))
    public void refundPayment(Long reservationId) {
        Payment payment = paymentRepository.findByReservationId(reservationId).orElseThrow(() -> new EntityNotFoundException("Payment not found with ID: " + reservationId));
        String token = refundService.getToken();
        refundService.cancelPayment(payment.getImpUid(), "예약 취소로 인한 환불", token);
        payment.cancelPaymentStatus(PaymentStatus.CANCELED);
        paymentRepository.save(payment);
    }

}
