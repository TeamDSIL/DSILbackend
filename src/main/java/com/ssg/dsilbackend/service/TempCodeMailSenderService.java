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

@Service
@RequiredArgsConstructor
@EnableAsync
public class TempCodeMailSenderService {
    private final JavaMailSender javaMailSender;

    @Async
    public void sendEmail(String email, String code) throws MessagingException {

        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("임시 인증 코드");
            helper.setText("귀하의 임시 인증 코드는: " + code);
        };
        try {
            this.javaMailSender.send(preparator);
        } catch (MailException e) {
            throw e;
        }
    }
}
