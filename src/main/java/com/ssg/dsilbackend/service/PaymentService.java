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

        private final ReserveService reserveService;

        public void savePayment(PaymentDTO paymentDTO) {
            try {
                //Members member = memberRepository.findById(reserveDTO.getMemberId()).orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + reserveDTO.getMemberId()));

                //Restaurant restaurant = restaurantRepository.findById(reserveDTO.getRestaurantId()).orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + reserveDTO.getRestaurantId()));
                Long memberId = 1L;

                Long restaurantId = 1L;

                Long reservationId = 1L;

                Members member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));

                Restaurant restaurant = restaurantListRepository.findById(restaurantId)
                        .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));

                Reservation reservation = reserveRepository.findById(reservationId)
                        .orElseThrow(() -> new EntityNotFoundException("Reserve not found with ID: " + restaurantId));


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
