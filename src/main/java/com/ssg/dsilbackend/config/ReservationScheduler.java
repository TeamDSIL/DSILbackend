package com.ssg.dsilbackend.config;

import com.ssg.dsilbackend.domain.Reservation;
import com.ssg.dsilbackend.dto.ReservationStateName;
import com.ssg.dsilbackend.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Component
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") //크론식 사용,매일 자정 실행
    public void deleteCanceledReservation() {
        LocalDateTime expiredData = LocalDateTime.now().minusDays(30);

        reservationRepository.deleteExpiredReservationsAndAssociatedReviews();
        reservationRepository.deleteExpiredReservation(expiredData);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") //크론식 사용
    public void updateReservationStatusToCompleted() {
        LocalDate now = LocalDate.now();
        int batchSize = 100; // 배치 사이즈 설정

        List<Reservation> reservations = reservationRepository.updateReservationStatusToCompleted(now, PageRequest.of(0, batchSize));

        for (Reservation reservation : reservations) {
            reservation.setReservationStateName(ReservationStateName.COMPLETED);
        }
        reservationRepository.saveAll(reservations);
    }
}
