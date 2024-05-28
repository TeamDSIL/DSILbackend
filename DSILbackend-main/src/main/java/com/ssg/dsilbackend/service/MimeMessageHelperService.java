package com.ssg.dsilbackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

/**
 * 해당 클래스는 회원에게 이메일을 보내주는 클래스로 이메일 전송 작업은 시간이 오래걸리기에
 * 비동기 메서드를 활성화하였고 메서드에 Async 어노테이션을 통해 비동기적으로 실행되게 구현
 * 작성자 : [Imhwan]
 */

@Service
@RequiredArgsConstructor
@EnableAsync
public class MimeMessageHelperService {

    private final JavaMailSender javaMailSender;

    @Async
    public void sendEmail(String email,String subject, String reservationInfo) throws MessagingException {

        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            // 콜백 메서드 구현
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                // MimeMessageHelper 생성
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                // 받는 사람 이메일 
                helper.setTo(email);
                // 이메일 제목
                helper.setSubject(subject);
                // 메일 내용
                helper.setText(reservationInfo);
            }
        };
        try {
            // 메일 전송
            this.javaMailSender.send(preparator);
        } catch (MailException e) {
            throw e;
        }
    }
}