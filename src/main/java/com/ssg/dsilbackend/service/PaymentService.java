package com.ssg.dsilbackend.service;


import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.domain.Payment;
import com.ssg.dsilbackend.domain.Reservation;
import com.ssg.dsilbackend.domain.Restaurant;
import com.ssg.dsilbackend.dto.payment.PaymentDTO;
import com.ssg.dsilbackend.repository.MemberRepository;
import com.ssg.dsilbackend.repository.PaymentRepository;
import com.ssg.dsilbackend.repository.ReserveRepository;
import com.ssg.dsilbackend.repository.RestaurantListRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
    public class PaymentService {

        private final PaymentRepository paymentRepository;

        private final ReserveRepository reserveRepository;

        private final RestaurantListRepository restaurantListRepository;

        private final MemberRepository memberRepository;


        public void savePayment(PaymentDTO paymentDTO,Long reservationId) {
            try {
                Long memberId = 30L;
                Long restaurantId = 1L;

                Members member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));

                Restaurant restaurant = restaurantListRepository.findById(restaurantId)
                        .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));

                Reservation reservation = reserveRepository.findById(reservationId)
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
                        .name(restaurant.getName())
                        .merchantUid(paymentDTO.getMerchantUid())
                        .reservation(reservation)
                        .build();

                paymentRepository.save(payment);
            } catch (Exception e) {
                log.error(e);
            }
        }
    }
