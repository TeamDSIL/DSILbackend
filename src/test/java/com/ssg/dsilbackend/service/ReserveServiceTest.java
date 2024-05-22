
package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.config.ReservationScheduler;
import com.ssg.dsilbackend.dto.reserve.ReserveDTO;
import com.ssg.dsilbackend.repository.PaymentRepository;
import com.ssg.dsilbackend.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;

import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class ReserveServiceTest {

    @Autowired
    private ReservationScheduler reservationScheduler;

    @Autowired
    private ReserveService reserveService;

    @Autowired
    private RefundService refundService;

    @Mock
    private Clock clock;

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    public void setUp() {
        // Set the clock to a fixed date for testing purposes
        Instant fixedInstant = LocalDate.of(2024, Month.APRIL, 19).atStartOfDay(ZoneId.systemDefault()).toInstant();
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    }

    @Test
    @DisplayName("CancelReservation")
    @Rollback(false)
    public void testCancelReservation() {
        Long reservationId = 132L;
        ReserveDTO reserveDTO = new ReserveDTO();
        reserveDTO.setReservationId(reservationId);
        reserveService.cancelReservation(reserveDTO.getReservationId());

    }

    @Test
    @DisplayName("스케줄러 테스트")
    @Transactional
    @Rollback(false)
    public void testUpdateReservationStatusToCompleted() {
        reservationScheduler.updateReservationStatusToCompleted();
    }

    @Test
    public void testGetToken() {
        try {
            String token = refundService.getToken();
            System.out.println("Generated Token: " + token);
            assert token != null && !token.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Rollback(value = false)
    public void testRefund() throws Exception {
        String token = refundService.getToken();
        String impUid = "imp_415887219118"; // 실제 imp_uid로 대체해야 합니다.
        String reason = "테스트 환불 사유";
        String response = refundService.cancelPayment(impUid, reason, token);
        System.out.println("Cancel Payment Response: " + response);
    }

    @Test
    @Rollback(value = false)
    public void testCancelReservationMailService() throws Exception {
        Long reservationId =43L;
        ReserveDTO reserveDTO = new ReserveDTO();
        reserveDTO.setReservationId(reservationId);
        reserveService.cancelReservation(reservationId);

    }
}
