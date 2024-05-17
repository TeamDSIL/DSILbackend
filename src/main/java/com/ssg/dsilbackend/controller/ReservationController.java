package com.ssg.dsilbackend.controller;

import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.dto.reserve.ReserveDTO;
import com.ssg.dsilbackend.repository.MemberRepository;
import com.ssg.dsilbackend.service.MimeMessageHelperService;
import com.ssg.dsilbackend.service.PaymentService;
import com.ssg.dsilbackend.service.ReserveService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//해당 컨트롤러는 예약과 관련된 내용을 DB에 저장 및 예약 시 고객에게 이메일을 날려주는 역활
@RestController
@RequestMapping("/restaurant")
@Log4j2
@RequiredArgsConstructor
public class ReservationController {

    private final ReserveService reservationService;

    @PostMapping("/detail")
    public ResponseEntity<?> createReservation(@RequestBody ReserveDTO reservationDTO) {
        try {

            log.info("reservationDate: {}", reservationDTO.getReservationDate());
            log.info("reservationTime: {}", reservationDTO.getReservationTime());
            log.info("peopleCount: {}", reservationDTO.getPeopleCount());
            //추가로 들어가야할건 restaurantId,memberId
            Long reservationId = reservationService.processReservation(reservationDTO);

            return ResponseEntity.ok(reservationId);
        } catch (Exception e) {
            log.error("Error creating reservation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating reservation");
        }
    }
}

//public class ReservationController {
//
//    private final ReserveService reservationService;
//
//    @PostMapping("/detail")
//    public ResponseEntity<?> createReservation(@RequestBody ReserveDTO reservationDTO) {
//        try {
//
//            log.info("reservationDate: {}", reservationDTO.getReservationDate());
//            log.info("reservationTime: {}", reservationDTO.getReservationTime());
//            log.info("peopleCount: {}", reservationDTO.getPeopleCount());
//            //추가로 들어가야할건 restaurantId,memberId
//            Long reservationId = reservationService.processReservation(reservationDTO);
//
//            //예약 완료시 email 보내줘야함
//            return ResponseEntity.ok(reservationId);
//        } catch (Exception e) {
//            log.error("Error creating reservation: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating reservation");
//        }
//    }
//}
