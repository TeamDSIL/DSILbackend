package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.config.ReservationScheduler;
import com.ssg.dsilbackend.domain.Reservation;
import com.ssg.dsilbackend.dto.ReservationStateName;
import com.ssg.dsilbackend.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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
        Long reservationId = 131L;
        reserveService.cancelReservation(reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + reservationId));

        // 예약 상태가 취소로 변경되었는지 확인
        Assertions.assertThat(reservation.getReservationStateName()).isEqualTo(ReservationStateName.CANCELED);

    }

    @Test
    @DisplayName("Test Scheduler for Deleting Canceled Reservations")
    @Rollback(false)
    public void testScheduler() {
        reservationScheduler.deleteCanceledReservation();
    }
}

