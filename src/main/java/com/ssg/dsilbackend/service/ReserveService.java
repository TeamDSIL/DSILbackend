package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.domain.Reservation;
import com.ssg.dsilbackend.domain.Restaurant;
import com.ssg.dsilbackend.dto.AvailableTimeTable;
import com.ssg.dsilbackend.dto.ReservationStateName;
import com.ssg.dsilbackend.dto.reserve.ReserveDTO;
import com.ssg.dsilbackend.repository.MemberRepository;
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
    private final PaymentService paymentService;

    public Long processReservation(ReserveDTO reserveDTO) {
        try {
            Members member = memberRepository.findById(reserveDTO.getMemberId())
                    .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + reserveDTO.getMemberId()));

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
                    .reservationTime(reserveDTO.getReservationTime())
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
                    reservationName, reservationDate, reservationTime.getTime(), peopleCount);

            String subject = "Dsil 서비스 예약 완료 알림";

            String email = savedReservation.getMembers().getEmail();
            mimeMessageHelperService.sendEmail(email,subject,reservationInfo);

            return reservationId;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error creating reservation", e);
        }
    }

    public void cancelReservation(Long reservationId) {
        try {
            Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new EntityNotFoundException("Reservation Not Found with ID: " + reservationId));
            reservation.setReservationStateName(ReservationStateName.CANCELED);
            reservationRepository.save(reservation);
            paymentService.refundPayment(reservationId);
            Members members = reservation.getMembers();
            String email = members.getEmail();
            String reservationName = reservation.getReservationName();
            LocalDate reservationDate = reservation.getReservationDate();
            AvailableTimeTable reservationTime = reservation.getReservationTime();

            String CancelReservationInfo = String.format(reservationName+ "고객님의 " + reservationDate +"일 " + reservationTime.getTime() +"시의 예약이 취소되었습니다.");
            String subject = "Dsil 서비스 예약 취소 알림";
            mimeMessageHelperService.sendEmail(email,subject, CancelReservationInfo);

        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException("Error canceling reservation", e);        }
    }
}
