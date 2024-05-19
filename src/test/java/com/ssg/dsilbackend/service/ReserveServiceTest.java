package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.config.ReservationScheduler;
import com.ssg.dsilbackend.dto.reserve.ReserveDTO;
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
    private ReservationRepository reservationRepository;

    @Autowired
    private ReserveService reserveService;

    @Mock
    private Clock clock;

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
        Long reservationId = 136L;
        ReserveDTO reserveDTO = new ReserveDTO();
        reserveDTO.setReservationId(reservationId);
        reserveService.cancelReservation(reserveDTO);

    }

    @Test
    @DisplayName("스케줄러 테스트")
    @Rollback(false)
    public void testScheduler() {
        reservationScheduler.deleteCanceledReservation();
    }
}

