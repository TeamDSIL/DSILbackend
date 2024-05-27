package com.ssg.dsilbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Log4j2
public class TempCodeService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final TempCodeMailSenderService tempCodeMailSenderService;

    private static final int CODE_LENGTH = 6;
    private static final String CODE_PREFIX = "tempCode:";

    public void sendEmailWithCode(String email) throws Exception {
        try {
            String code = generateTempCode();
            storeCodeInRedis(email, code);
            logCodeFromRedis(email); // 코드 로그 기록 추가
            tempCodeMailSenderService.sendEmail(email, code); // 변경된 메서드 호출
        } catch (Exception e) {
            // 구체적인 예외를 로그에 기록
            throw new Exception("Error occurred while sending email with code", e);
        }
    }

    public boolean verifyCode(String email, String code) {
        String key = CODE_PREFIX + email;
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Object storedCode = operations.get(key);

        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    private void storeCodeInRedis(String email, String code) {
        String key = CODE_PREFIX + email;
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set(key, code, Duration.ofMinutes(10)); // 10분 동안 유효
    }

    private void logCodeFromRedis(String email) {
        String key = CODE_PREFIX + email;
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Object storedCode = operations.get(key);
        log.info("Stored code for {} is {}", email, storedCode); // 로그에 코드 기록
    }

    private String generateTempCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10)); // 0-9 사이의 숫자 추가
        }
        return code.toString();
    }
}
