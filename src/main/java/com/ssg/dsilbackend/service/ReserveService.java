package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.domain.*;
import com.ssg.dsilbackend.dto.AvailableTimeTable;
import com.ssg.dsilbackend.dto.ReservationStateName;
import com.ssg.dsilbackend.dto.reserve.ReserveDTO;
import com.ssg.dsilbackend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * 해당 클래스는 예약 생성 및 취소를 위해 사용한 예약 관련 서비스 코드다.
 * 작성자 : [Imhwan]
 */
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
    private final PointManageRepository pointManageRepository;
    private final PaymentRepository paymentRepository;

    //예약 생성 메서드
    public Long processReservation(ReserveDTO reserveDTO) {
        try {
            Members member = memberRepository.findById(reserveDTO.getMemberId())
                    .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + reserveDTO.getMemberId()));

            Restaurant restaurant = restaurantRepository.findById(reserveDTO.getRestaurantId())
                    .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + reserveDTO.getRestaurantId()));

            if (restaurant.getTableCount() <= 0) {
                throw new RuntimeException("예약 가능한 테이블이 없습니다");
            }

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

            int people = reserveDTO.getPeopleCount();
            int tables = (int) Math.ceil((double) people / 4); // 예약한 인원을 4로 나눈 후 올림 처리하여 필요한 테이블 수 계산
            restaurant.reduceTable((long) tables);

            //예약이 성공했을 경우 해당 회원에게 100포인트 적립
            Point point = member.getPoint();
            Long currentPoint = point.getCurrentPoint();
            Long accumulatePoint = point.getAccumulatePoint();
            point.setCurrentPoint(currentPoint + 100);
            point.setAccumulatePoint(accumulatePoint + 100);
            pointManageRepository.save(point);

            //이메일로 예약 정보를 보내주기 위해 변수 추출
            LocalDate reservationDate = savedReservation.getReservationDate();
            AvailableTimeTable reservationTimeEnum = savedReservation.getReservationTime();
            String reservationTime = enumToTime(reservationTimeEnum);
            int peopleCount = savedReservation.getPeopleCount();

            String reservationInfo = String.format("방문 고객 : %s\n예약 날짜는 : %s이며 \n예약 시간은 %s이고 \n예약 인원 수는 %d명입니다",
                    reservationName, reservationDate, reservationTime, peopleCount);

            String subject = "Dsil 서비스 예약 완료 알림";

            String email = savedReservation.getMembers().getEmail();
            mimeMessageHelperService.sendEmail(email, subject, reservationInfo);

            return reservationId;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error creating reservation", e);
        }
    }

    //예약 취소 메서드
    public void cancelReservation(Long reservationId) {
        try {
            Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new EntityNotFoundException("Reservation Not Found with ID: " + reservationId));

            Restaurant restaurant = reservation.getRestaurant();
            int people = reservation.getPeopleCount();
            int tables = (int) Math.ceil((double) people / 4);
            restaurant.recoverTable((long) tables);

            reservation.setReservationStateName(ReservationStateName.CANCELED);
            reservationRepository.save(reservation);

            Members members = reservation.getMembers();

            //예약 취소 이메일 전송을 위한 변수 추출
            String email = members.getEmail();
            String reservationName = reservation.getReservationName();
            LocalDate reservationDate = reservation.getReservationDate();
            AvailableTimeTable reservationTimeEnum = reservation.getReservationTime();
            String reservationTime = enumToTime(reservationTimeEnum);

            String CancelReservationInfo = String.format(reservationName + "고객님의 " + reservationDate + "일 " + reservationTime  + "시의 예약이 취소되었습니다.");
            String subject = "Dsil 서비스 예약 취소 알림";
            mimeMessageHelperService.sendEmail(email, subject, CancelReservationInfo);

            Payment payment = paymentRepository.findByReservationId(reservationId).orElse(null);

            Point point = members.getPoint();
            Long pointUsage = payment != null ? payment.getPointUsage() : 0L;

            if (payment != null && payment.getImpUid() != null) {
                // 일반 결제 취소
                paymentService.refundPayment(reservationId);
            }

            // 예약 취소 시 예약할 때 주는 100포인트 몰수 및 포인트 환불 처리
            point.setCurrentPoint(point.getCurrentPoint() + pointUsage - 100);
            point.setAccumulatePoint(point.getAccumulatePoint() - 100);
            pointManageRepository.save(point);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Error canceling reservation", e);
        }
    }

    //프론트에서 enum타입으로 넘어오는 값 이메일 정보에 ex)13:00으로 출력하기 위해 변환
    //원래는 Enum에서 변환해야함
    private String enumToTime(AvailableTimeTable timeEnum) {
        // Enum 값에서 시간 문자열로 변환
        return switch (timeEnum) {
            case AFTERNOON_12 -> "12:00";
            case AFTERNOON_1 -> "13:00";
            case AFTERNOON_2 -> "14:00";
            case AFTERNOON_3 -> "15:00";
            case AFTERNOON_4 -> "16:00";
            case AFTERNOON_5 -> "17:00";
            case AFTERNOON_6 -> "18:00";
            case AFTERNOON_7 -> "19:00";
            case AFTERNOON_8 -> "20:00";
            case AFTERNOON_9 -> "21:00";
            default -> throw new IllegalArgumentException("Unknown time enum: " + timeEnum);
        };
    }
}
