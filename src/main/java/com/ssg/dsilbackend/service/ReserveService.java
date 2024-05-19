package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.domain.Payment;
import com.ssg.dsilbackend.domain.Reservation;
import com.ssg.dsilbackend.domain.Restaurant;
import com.ssg.dsilbackend.dto.AvailableTimeTable;
import com.ssg.dsilbackend.dto.PaymentStatus;
import com.ssg.dsilbackend.dto.ReservationStateName;
import com.ssg.dsilbackend.dto.reserve.ReserveDTO;
import com.ssg.dsilbackend.repository.MemberRepository;
import com.ssg.dsilbackend.repository.PaymentRepository;
import com.ssg.dsilbackend.repository.ReservationRepository;
import com.ssg.dsilbackend.repository.RestaurantListRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class ReserveService {

    private final MemberRepository memberRepository;
    private final RestaurantListRepository restaurantRepository;
    private final MimeMessageHelperService mimeMessageHelperService;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public Long processReservation(ReserveDTO reserveDTO) {
        try {
            Long memberId = 44L;

            Members member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));

            Restaurant restaurant = restaurantRepository.findById(reserveDTO.getRestaurantId())
                    .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + reserveDTO.getRestaurantId()));

            String name = member.getName();
            String phone = member.getTel();

            String reservationName = (reserveDTO.getReservationName() == null || reserveDTO.getReservationName().isEmpty())
                    ? name : reserveDTO.getReservationName();

            String reservationTel = (reserveDTO.getReservationTel() == null || reserveDTO.getReservationTel().isEmpty())
                    ? phone : reserveDTO.getReservationTel();

            Reservation reservation = Reservation.builder()
                    .createdTime(LocalDateTime.now())
                    .reservationTime(AvailableTimeTable.AFTERNOON_1)
                    .reservationDate(reserveDTO.getReservationDate())
                    .peopleCount(reserveDTO.getPeopleCount())
                    .reservationStateName(ReservationStateName.RESERVED)
                    .members(member)
                    .restaurant(restaurant)
                    .requestContent(reserveDTO.getRequestContent())
                    .reservationTel(reservationTel)
                    .reservationName(reservationName)
                    .build();

            Reservation savedReservation = reservationRepository.save(reservation);
            Long reservationId = savedReservation.getId();
            log.info("예약 성공 : {}", reservationId);

            LocalDate reservationDate = savedReservation.getReservationDate();
            AvailableTimeTable reservationTime = savedReservation.getReservationTime();
            int peopleCount = savedReservation.getPeopleCount();

            String reservationInfo = String.format("방문 고객 : %s\n예약 날짜는 : %s이며 \n예약 시간은 %s이고 \n예약 인원 수는 %d명입니다",
                    reservationName, reservationDate, reservationTime, peopleCount);

            String email = savedReservation.getMembers().getEmail();
            mimeMessageHelperService.sendEmail(email, reservationInfo);

            return reservationId;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error creating reservation", e);
        }
    }

    public void cancelReservation(ReserveDTO reserveDTO) {
        try {
            Long reservationId = reserveDTO.getReservationId();
            Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new EntityNotFoundException("Reservation Not Found with ID: " + reservationId));
            reservation.cancelReservationStateName(ReservationStateName.CANCELED);
            reservationRepository.save(reservation);
            Payment payment = paymentRepository.findByReservation(reservation).orElseThrow(() -> new EntityNotFoundException("Payment Not Found with ID: " + reservationId));
            payment.cancelPaymentStatus(PaymentStatus.CANCELED);
            paymentRepository.save(payment);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException("Error canceling reservation", e);        }
    }
}
