package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.config.ReservationScheduler;
import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.domain.Restaurant;
import com.ssg.dsilbackend.dto.AvailableTimeTable;
import com.ssg.dsilbackend.dto.reserve.ReserveDTO;
import com.ssg.dsilbackend.repository.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@Log4j2
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
    private RestaurantRepository restaurantRepository1;

    @Mock
    private RestaurantListRepository restaurantRepository;

    @InjectMocks
    private ReserveService reserveService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PointManageRepository pointManageRepository;

    @Mock
    private MimeMessageHelperService mimeMessageHelperService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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
            log.info(token);
            assert token != null && !token.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Rollback(value = false)
    public void testRefund() throws Exception {
        String token = refundService.getToken();
        String impUid = "imp_652496456041";
        String reason = "테스트 환불";
        String response = refundService.cancelPayment(impUid, reason, token);
        log.info(response);
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
    public void testConcurrentReservation() throws InterruptedException, ExecutionException {
        // Mock 회원 객체 생성
        Members member = Members.builder()
                .id(68L)
                .name("TestUser")
                .tel("01012345678")
                .email("test@example.com")
                .build();

        Restaurant restaurant = Restaurant.builder()
                .id(2L)
                .tableCount(4L)
                .name("TestRestaurant")
                .build();
        // MemberRepository의 findById 메서드에 대한 Mock 설정
        when(memberRepository.findById(68L)).thenReturn(Optional.of(member));

        when(restaurantRepository.findById(2L)).thenReturn(Optional.of(restaurant));

        // 테스트 로직 실행
        int concurrencyLevel = 10; // 동시성 레벨 설정
        LocalDate reservationDate = LocalDate.now().plusDays(1); // 내일 날짜로 설정
        AvailableTimeTable reservationTime = AvailableTimeTable.AFTERNOON_9; // 시간대 설정
        CompletableFuture<?>[] futures = new CompletableFuture[concurrencyLevel];

        for (int i = 0; i < concurrencyLevel; i++) {
            ReserveDTO reserveDTO = new ReserveDTO();
            reserveDTO.setMemberId(68L); // 회원 ID를 68번으로 설정
            reserveDTO.setRestaurantId(2L);
            reserveDTO.setReservationDate(reservationDate);
            reserveDTO.setReservationTime(reservationTime); // 같은 시간대로 설정

            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    reserveService.processReservation(reserveDTO);
                } catch (Exception e) {
                    // 예외 처리: 로그를 남기거나 특정 작업을 수행할 수 있습니다.
                    log.error("Error during reservation", e);
                }
            });
        }

        // 모든 CompletableFuture가 완료될 때까지 대기
        CompletableFuture.allOf(futures).get();

        // 여기에 예상 결과를 확인하는 assert 구문 등을 추가하세요
    }
}