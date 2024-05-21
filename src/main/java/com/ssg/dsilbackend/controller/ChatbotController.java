package com.ssg.dsilbackend.controller;

import com.ssg.dsilbackend.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/chatbot")
public class ChatbotController {
    // ChatbotService를 주입받습니다.
    @Autowired
    private ChatbotService chatbotService;

    // 사용자가 보낸 메시지를 받아서 ChatbotService를 통해 클로바 챗봇 API에 전달하고 응답을 반환합니다.
    @PostMapping("/send")
    public String sendMessage(@RequestParam String message) {
        return chatbotService.sendMessageToChatbot(message);
    }
}
