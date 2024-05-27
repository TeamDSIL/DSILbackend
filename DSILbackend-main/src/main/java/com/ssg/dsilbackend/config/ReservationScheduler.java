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

/**
 * 해당 클래스는 자동화를 위해 사용하였고 스케줄러와 배치를 사용하였다.
 * 작성자 : [Imhwan]
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Component
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;

    //취소 상태의 예약를 30일 동안 보관하고 삭제하는 로직이다.(리뷰는 외래키 참조때문에 같이 삭제)
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") //크론식 사용,매일 자정 실행
    public void deleteCanceledReservation() {
        LocalDateTime expiredData = LocalDateTime.now().minusDays(30);

        reservationRepository.deleteExpiredReservationsAndAssociatedReviews();
        reservationRepository.deleteExpiredReservation(expiredData);
    }

    //해당 코드는 예약된 상태의 예약들을 자동으로 완료 상태로 변환시키기 위해 도입하였다.
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
