package com.ssg.dsilbackend.config;

import com.ssg.dsilbackend.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Log4j2
@RequiredArgsConstructor
public class RefreshTokenScheduler {

    private final RefreshRepository refreshRepository;

    @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
    public void cleanupExpiredTokens() {
        log.info("Starting cleanup of expired refresh tokens");

        LocalDateTime now = LocalDateTime.now();
        String nowStr = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        refreshRepository.deleteByExpirationBefore(nowStr);

        log.info("Cleanup of expired refresh tokens completed");
    }
}
