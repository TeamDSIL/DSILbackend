package com.ssg.dsilbackend.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Log4j2
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        String email = obtainUsername(req);
        String password = obtainPassword(req);

        CustomAuthenticationToken authToken = new CustomAuthenticationToken(email, password, email);
        return authenticationManager.authenticate(authToken);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        jwtUtil.createJWT(response, authentication);
        response.setStatus(HttpStatus.OK.value());
        log.info("인증 성공 - JWT 생성 및 설정 완료");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        if (failed.getMessage().equals("해당 계정은 비활성화 상태입니다.")) {
            response.getWriter().write("해당 계정은 비활성화 상태입니다.");
        } else {
            response.getWriter().write("인증 실패 - " + failed.getMessage());
        }
        log.warn("인증 실패 - {}", failed.getMessage());
    }
}
