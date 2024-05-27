package com.ssg.dsilbackend.controller;

import com.ssg.dsilbackend.dto.reserve.ReserveDTO;
import com.ssg.dsilbackend.service.ReserveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 해당 컨트롤러는 예약 정보를 POST로 요청 받으면 해당 예약 정보를 저장하고 성공 여부에 따라 HTTP 응답을 반환해준다
 * 작성자 : [Imhwan}
 */
@RestController
@RequestMapping("/restaurant")
@Log4j2
@RequiredArgsConstructor
public class ReservationController {

    private final ReserveService reservationService;

    @PostMapping("/detail")
    public ResponseEntity<?> createReservation(@RequestBody ReserveDTO reservationDTO) {
        try {
            Long reservationId = reservationService.processReservation(reservationDTO);

            return ResponseEntity.ok(reservationId);
        } catch (Exception e) {
            log.error("Error creating reservation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating reservation");
        }
    }

}
