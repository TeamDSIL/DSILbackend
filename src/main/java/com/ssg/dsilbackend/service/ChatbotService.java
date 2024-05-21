package com.ssg.dsilbackend.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatbotService {
    // application.properties 파일에서 chatbot.api.url 값을 가져옵니다.
    @Value("${chatbot.api.url}")
    private String apiUrl;

    // application.properties 파일에서 chatbot.api.key 값을 가져옵니다.
    @Value("${chatbot.api.key}")
    private String apiKey;

    // 사용자가 보낸 메시지를 클로바 챗봇 API에 전달하고 응답을 반환하는 메서드입니다.
    public String sendMessageToChatbot(String message) {
        // RestTemplate을 사용하여 HTTP 요청을 보냅니다.
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-NCP-CHATBOT_SIGNATURE", apiKey);

        // 챗봇 API 요청에 필요한 데이터를 설정합니다.
        Map<String, Object> request = new HashMap<>();
        request.put("version", "v2");
        request.put("userId", "user-id-1234");
        request.put("timestamp", System.currentTimeMillis());
        Map<String, String> data = new HashMap<>();
        data.put("description", message);
        Map<String, Object> bubble = new HashMap<>();
        bubble.put("type", "text");
        bubble.put("data", data);
        request.put("bubbles", new Object[]{bubble});
        request.put("event", "send");

        // HTTP 엔티티를 생성하고, 클로바 챗봇 API에 POST 요청을 보냅니다.
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        // API 응답을 반환합니다.
        return response.getBody();
    }
}

//바로 여기부터 챗봇에 대한 레퍼런스를 실시하는 중입니다
//현재시각 5월 20일 오후 3시 39분