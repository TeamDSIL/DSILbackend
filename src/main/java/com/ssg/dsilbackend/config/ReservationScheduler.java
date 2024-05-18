package com.ssg.dsilbackend.config;

import com.ssg.dsilbackend.dto.ReservationStateName;
import com.ssg.dsilbackend.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Component
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") //크론식 사용
    public void deleteCanceledReservation() {
        LocalDateTime expiredData = LocalDateTime.now().minusDays(30);

        reservationRepository.deleteExpiredReservationsAndAssociatedReviews();
        reservationRepository.deleteExpiredReservation(expiredData);
    }

}
