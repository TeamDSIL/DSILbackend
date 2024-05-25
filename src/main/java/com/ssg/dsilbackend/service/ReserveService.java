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

            if(restaurant.getTableCount()<=0){
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
            restaurant.reduceTable((long)tables);

            //예약이 성공했을 경우 해당 회원에게 100포인트 적립
            Point point = member.getPoint();
            Long currentPoint = point.getCurrentPoint();
            Long accumulatePoint = point.getAccumulatePoint();
            point.setCurrentPoint(currentPoint+100);
            point.setAccumulatePoint(accumulatePoint+100);
            pointManageRepository.save(point);

            //이메일로 예약 정보를 보내주기 위해 변수 추출
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

    //예약 취소 메서드
    public void cancelReservation(Long reservationId) {
        try {
            Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new EntityNotFoundException("Reservation Not Found with ID: " + reservationId));


            reservation.setReservationStateName(ReservationStateName.CANCELED);
            reservationRepository.save(reservation);

            Members members = reservation.getMembers();

            //예약 취소 이메일 전송을 위한 변수 추출
            String email = members.getEmail();
            String reservationName = reservation.getReservationName();
            LocalDate reservationDate = reservation.getReservationDate();
            AvailableTimeTable reservationTime = reservation.getReservationTime();

            String CancelReservationInfo = String.format(reservationName+ "고객님의 " + reservationDate +"일 " + reservationTime.getTime() +"시의 예약이 취소되었습니다.");
            String subject = "Dsil 서비스 예약 취소 알림";
            mimeMessageHelperService.sendEmail(email,subject, CancelReservationInfo);

            Payment payment = paymentRepository.findByReservationId(reservationId).orElse(null);

            if(payment!=null){
                paymentService.refundPayment(reservationId);
                Point point = members.getPoint();
                Long pointUsage = payment.getPointUsage();

                //예약 취소 시 예약할떄 주는 100포인트 몰수
                    point.setCurrentPoint(point.getCurrentPoint()+pointUsage-100);
                    point.setAccumulatePoint(point.getAccumulatePoint()-100);
                    pointManageRepository.save(point);
                }else if (payment==null){
                Point point = members.getPoint();

                //예약 취소 시 예약할떄 주는 100포인트 몰수
                point.setCurrentPoint(point.getCurrentPoint()-100);
                point.setAccumulatePoint(point.getAccumulatePoint()-100);
                pointManageRepository.save(point);
            }

        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException("Error canceling reservation", e);        }
    }
}
