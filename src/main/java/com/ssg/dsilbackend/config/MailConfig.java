package com.ssg.dsilbackend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * 해당 클래스는 메일 서버 설정을 구성하기 위해 사용한 클래스로 application.properties에서 해당 정보들을 가져온다.
 * 작성자: [Imhwan]
 */
@Configuration
@ConfigurationProperties(prefix = "mail")
@Getter
@Setter
public class MailConfig {

    private String host;
    private int port;
    private String username;
    private String password;
    private Properties properties;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.putAll(properties);

        return mailSender;
    }
}