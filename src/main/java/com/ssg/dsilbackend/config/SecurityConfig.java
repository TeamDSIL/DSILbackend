package com.ssg.dsilbackend.config;

//import com.ssg.dsilbackend.jwt.JWTFilter;
//import com.ssg.dsilbackend.jwt.JWTUtil;
//import com.ssg.dsilbackend.jwt.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //AuthenticationManager 가 인자로 받을 AuthenticationConfiguration 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    //JWTUtil 주입
//    private final JWTUtil jwtUtil;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        // cors 설정
//        http
//                .cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
//
//                    @Override
//                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//
//                        CorsConfiguration configuration = new CorsConfiguration();
//
//                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
//                        configuration.setAllowedMethods(Collections.singletonList("*"));
//                        configuration.setAllowCredentials(true);
//                        configuration.setAllowedHeaders(Collections.singletonList("*"));
//                        configuration.setMaxAge(3600L);
//
//                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));
//
//                        return configuration;
//                    }
//                })));

        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //경로별 인가 작업

//        http
//                .authorizeHttpRequests((auth) -> auth
//                        .requestMatchers("/login", "/", "/join").permitAll()
//                        .requestMatchers("/admin").hasRole("ADMIN")
//                        .anyRequest().authenticated());

//        http
//                .addFilterAt(new JWTFilter(jwtUtil), LoginFilter.class);

//        http
//                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //세션 설정
//        http
//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }
}
