package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.config.ReservationScheduler;
import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.domain.Reservation;
import com.ssg.dsilbackend.domain.Restaurant;
import com.ssg.dsilbackend.dto.AvailableTimeTable;
import com.ssg.dsilbackend.dto.reserve.ReserveDTO;
import com.ssg.dsilbackend.repository.MemberRepository;
import com.ssg.dsilbackend.repository.PointManageRepository;
import com.ssg.dsilbackend.repository.ReservationRepository;
import com.ssg.dsilbackend.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class ReserveServiceTest {

    @Autowired
    private ReservationScheduler reservationScheduler;

//    @Autowired
//    private ReserveService reserveService;

    @Autowired
    private RefundService refundService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PointManageRepository pointManageRepository;

    @Mock
    private MimeMessageHelperService mimeMessageHelperService;

    @InjectMocks
    private ReserveService reserveService;


    @Mock
    private Clock clock;

    @Test
    @DisplayName("CancelReservation")
    @Rollback(false)
    public void testCancelReservation() {
        Long reservationId = 37L;
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
        String impUid = "imp_652496456041"; // 실제 imp_uid로 대체해야 합니다.
        String reason = "테스트 환불 사유";
        String response = refundService.cancelPayment(impUid, reason, token);
        System.out.println("Cancel Payment Response: " + response);
    }

    @Test
    @Rollback(value = false)
    public void testCancelReservationMailService() throws Exception {
        Long reservationId = 40L;
        ReserveDTO reserveDTO = new ReserveDTO();
        reserveDTO.setReservationId(reservationId);
        reserveService.cancelReservation(reservationId);
    }

    @Test
    @DisplayName("예약 동시에 들어올 때 테스트")
    public void ConcurrentReservation() {
        MockitoAnnotations.openMocks(this);

        // 첫 번째 예약 요청에 대한 DTO 생성
        LocalDate reservationDate = LocalDate.now().plusDays(3);
        ReserveDTO reserveDTO1 = new ReserveDTO();
        reserveDTO1.setMemberId(59L); // 회원 59번
        reserveDTO1.setRestaurantId(2L); // 식당 ID 설정
        reserveDTO1.setReservationDate(reservationDate); // 예약 날짜 설정
        reserveDTO1.setReservationTime(AvailableTimeTable.AFTERNOON_9); // 예약 시간 설정
        reserveDTO1.setPeopleCount(4); // 예약 인원 수 설정

        // 두 번째 예약 요청에 대한 DTO 생성
        ReserveDTO reserveDTO2 = new ReserveDTO();
        reserveDTO2.setMemberId(60L); // 회원 60번
        reserveDTO2.setRestaurantId(2L); // 식당 ID 설정
        reserveDTO2.setReservationDate(reservationDate); // 예약 날짜 설정
        reserveDTO2.setReservationTime(AvailableTimeTable.AFTERNOON_9); // 예약 시간 설정
        reserveDTO2.setPeopleCount(3); // 예약 인원 수 설정

        // Mock 설정
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true, false);
        when(valueOperations.get(anyString())).thenReturn("1");

        // Mock 멤버와 레스토랑 리포지토리
        when(memberRepository.findById(59L)).thenReturn(Optional.of(mock(Members.class)));
        when(memberRepository.findById(60L)).thenReturn(Optional.of(mock(Members.class)));
        when(restaurantRepository.findById(2L)).thenReturn(Optional.of(mock(Restaurant.class)));

        // Mock 리저베이션 리포지토리
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            return reservation;
        });

        // Mock Redis 스크립트
        when(redisTemplate.execute(any(RedisCallback.class))).thenReturn(1L);

        // 스레드 풀 생성
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            try {
                reserveService.processReservation(reserveDTO1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            try {
                reserveService.processReservation(reserveDTO2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 두 CompletableFuture가 완료될 때까지 대기
        CompletableFuture.allOf(future1, future2).join();

        // 확인
        verify(reservationRepository, atLeastOnce()).save(any(Reservation.class));
        verify(redisTemplate, atLeastOnce()).delete("lock:restaurant:2");
    }
}