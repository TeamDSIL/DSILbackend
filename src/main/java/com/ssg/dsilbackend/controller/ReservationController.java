package com.ssg.dsilbackend.controller;

import com.ssg.dsilbackend.domain.Restaurant;
import com.ssg.dsilbackend.dto.reserve.ReserveDTO;
import com.ssg.dsilbackend.repository.RestaurantRepository;
import com.ssg.dsilbackend.service.ReserveService;
import jakarta.persistence.EntityNotFoundException;
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
    private final RestaurantRepository restaurantRepository;
    private final ReserveService reserveService;

    //예약 생성 과정
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

    //인원 수를 기준으로 일단 테이블을 먼저 선점
    @PostMapping("/reservetable")
    public ResponseEntity<?> createReservationTable(@RequestBody ReserveDTO reservationDTO) {
        Long restaurantId = reservationDTO.getRestaurantId();
        int numberOfTables = reservationDTO.getNumberOfTables();
        log.info(numberOfTables);
        try {
            reserveService.reserveTable(restaurantId,numberOfTables);
            return ResponseEntity.status(HttpStatus.CREATED).body("테이블 먼저 차감");
        } catch (Exception e) {
            log.error("테이블 선점 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reducing tables");
        }
    }

    //닫기 버튼 누를 시 선점했던 테이블 반환
    @PostMapping("/cancelreservation")
    public ResponseEntity<?> cancelReservation(@RequestBody ReserveDTO reservationDTO) {
        Long restaurantId = reservationDTO.getRestaurantId();
        int numberOfTables = reservationDTO.getNumberOfTables();

        try {
            reserveService.cancelTableReservation(restaurantId, numberOfTables);
            return ResponseEntity.status(HttpStatus.OK).body("테이블 복원 완료");
        }catch (Exception e){
            log.error("테이블 복원 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error restoring tables");
        }
    }

}
