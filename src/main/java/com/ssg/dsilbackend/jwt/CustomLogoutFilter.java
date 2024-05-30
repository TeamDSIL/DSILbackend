package com.ssg.dsilbackend.jwt;

import com.ssg.dsilbackend.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
@Log4j2
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Get refresh token from cookies
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    refresh = cookie.getValue();
                    break;
                }
            }
        }

        log.debug("Refresh Token from cookie: {}", refresh);

        // Refresh token null check
        if (refresh == null) {
            log.warn("Refresh token is null");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            log.warn("Refresh token is expired");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Check if the token is a refresh token
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            log.warn("Token is not a refresh token");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Check if the token exists in the database
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            log.warn("Refresh token does not exist in the database");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

//         Proceed with logout
//         Remove refresh token from the database
        refreshRepository.deleteByRefresh(refresh);
        log.info("Refresh token removed from the database");

//        리프레시 토큰은 시간이 지나면 자동 소멸 24시간

        // Remove refresh token cookie
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
