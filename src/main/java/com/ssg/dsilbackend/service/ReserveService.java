package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.domain.Reservation;
import com.ssg.dsilbackend.domain.Restaurant;
import com.ssg.dsilbackend.dto.AvailableTimeTable;
import com.ssg.dsilbackend.dto.ReservationStateName;
import com.ssg.dsilbackend.dto.reserve.ReserveDTO;
import com.ssg.dsilbackend.repository.MemberRepository;
import com.ssg.dsilbackend.repository.ReserveRepository;
import com.ssg.dsilbackend.repository.RestaurantListRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Transactional
@RequiredArgsConstructor
@Log4j2
@Service
@Getter
public class ReserveService {

    private final ReserveRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final RestaurantListRepository restaurantRepository;
    private final MimeMessageHelperService mimeMessageHelperService;

    public Long processReservation(ReserveDTO reserveDTO) {
        try {

            Long memberId = 44L;
            Long restaurantId = 2L;

            Members member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));

            Restaurant restaurant = restaurantRepository.findById(restaurantId)
                    .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));

            String name = member.getName();

            String phone = member.getTel();

            String reservationName;

            String reservationTel;

            if (reserveDTO.getReservationName() == null || reserveDTO.getReservationName().isEmpty()) {
                reservationName = name; // 예약명이 없는 경우 회원의 이름 사용
            } else {
                reservationName = reserveDTO.getReservationName();
            }

            if(reserveDTO.getReservationTel() == null || reserveDTO.getReservationTel().isEmpty()) {
                reservationTel = phone;
            }else {
                reservationTel = reserveDTO.getReservationTel();
            }

            Reservation reservation = Reservation.builder()
                    .reservationTime(AvailableTimeTable.AFTERNOON_1)
                    .reservationDate(reserveDTO.getReservationDate())
                    .peopleCount(reserveDTO.getPeopleCount())
                    .reservationStateName(ReservationStateName.RESERVED)
                    .restaurant(restaurant)
                    .members(member)
                    .requestContent(reserveDTO.getRequestContent())
                    .reservationTel(reservationTel)
                    .reservationName(reservationName)
                    .build();

            Reservation savedReservation = reservationRepository.save(reservation);
            Long reservationId = savedReservation.getId();
            log.info("예약 성공 : {}", reservationId);

            LocalDate reservationDate = reservation.getReservationDate();
            AvailableTimeTable reservationTime = reservation.getReservationTime();
            int peopleCount = reservation.getPeopleCount();

            String reservationInfo = "방문 고객 : " + reservationName +"\n예약 날짜는 : " + reservationDate + "이며 \n" + "예약 시간은 " + reservationTime + "이고 \n" + "예약 인원 수는 " + peopleCount + "명입니다";

            String email = reservation.getMembers().getEmail();

            mimeMessageHelperService.sendEmail(email, reservationInfo);

            return reservationId;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error creating reservation", e);
        }
    }
}
