package com.ssg.dsilbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/clova")
public class ClovaChatbotController {

    @PostMapping("/reservation")
    public ResponseEntity<Map<String, Object>> handleClovaRequest(@RequestBody Map<String, Object> requestBody) {
        Map<String, Object> userRequest = (Map<String, Object>) requestBody.get("userRequest");
        String userMessage = (String) userRequest.get("utterance");

        Map<String, Object> responseBody = new HashMap<>();

        if (userMessage.contains("예약")) {
            // 예약 처리 로직 호출
            String reservationResponse = makeReservation();
            responseBody.put("version", "2.0");
            Map<String, Object> template = new HashMap<>();
            Map<String, Object> output = new HashMap<>();
            Map<String, String> simpleText = new HashMap<>();
            simpleText.put("text", reservationResponse);
            output.put("simpleText", simpleText);
            template.put("outputs", Collections.singletonList(output));
            responseBody.put("template", template);
        } else {
            responseBody.put("version", "2.0");
            Map<String, Object> template = new HashMap<>();
            Map<String, Object> output = new HashMap<>();
            Map<String, String> simpleText = new HashMap<>();
            simpleText.put("text", "안녕하세요, 무엇을 도와드릴까요?");
            output.put("simpleText", simpleText);
            template.put("outputs", Collections.singletonList(output));
            responseBody.put("template", template);
        }

        return ResponseEntity.ok(responseBody);
    }

    private String makeReservation() {
        // 예약 처리 로직 구현
        // 예를 들어, JPA를 사용하여 예약 데이터를 저장
        return "예약이 완료되었습니다!";
    }
}