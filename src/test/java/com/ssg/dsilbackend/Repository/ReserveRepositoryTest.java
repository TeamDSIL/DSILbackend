package com.ssg.dsilbackend.Repository;


import com.ssg.dsilbackend.domain.Reservation;
import com.ssg.dsilbackend.dto.reserve.ReserveDTO;
import com.ssg.dsilbackend.repository.ReserveRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.Fail.fail;

@SpringBootTest
public class ReserveRepositoryTest {
//    @Autowired
//    private ReserveService reserveService;
//
//    @Autowired
//    private ReserveRepository reserveRepository;
//
//    @Test
//    public void testProcessReservation() {
//        // 테스트에 필요한 데이터를 준비합니다.
//        ReserveDTO reserveDTO = new ReserveDTO();
//        reserveDTO.setRestaurantId(1L); // 레스토랑 ID 설정
//        reserveDTO.setMemberId(1L); // 회원 ID 설정
//        reserveDTO.setReservationStateName("RESERVED"); // 예약 상태 설정
//        reserveDTO.setPeopleCount(4); // 예약 인원 설정
//        reserveDTO.setReservationTime("12:40 PM"); // 예약 시간 설정
//        reserveDTO.setReservationName("John Doe11"); // 예약자 이름 설정
//        reserveDTO.setRequestContent("Special request"); // 요청 사항 설정
//        reserveDTO.setReservationTel("123-456-7890"); // 예약자 전화번호 설정
//
//        // 예약 서비스의 processReservation 메서드를 호출합니다.
//        try {
//            reserveService.processReservation(reserveDTO);
//
//            // 예약이 성공적으로 처리되었는지 확인합니다.
//            List<Reservation> reservations = reserveRepository.findAll();
//            assertThat(reservations).isNotEmpty();
//            assertThat(reservations).hasSizeGreaterThan(0);
//        } catch (Exception e) {
//            // 예외가 발생하면 테스트를 실패로 처리합니다.
//            fail("예약 서비스의 processReservation 메서드에서 예외가 발생했습니다: " + e.getMessage());
//        }
//    }
}

