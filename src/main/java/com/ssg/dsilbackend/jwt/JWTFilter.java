package com.ssg.dsilbackend.jwt;

import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.domain.Permission;
import com.ssg.dsilbackend.dto.PermissionRole;
import com.ssg.dsilbackend.security.CustomUserDetails;
import com.ssg.dsilbackend.repository.PermissionManageRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
@Log4j2
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final PermissionManageRepository permissionManageRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    authorization = cookie.getValue();
                    log.info("Refresh token from cookie: {}", authorization);
                }
            }
        }

        if (authorization == null) {
            authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.startsWith("Bearer ")) {
                authorization = authorization.substring(7);
                log.info("Access token from header: {}", authorization);
            }
        }

        if (authorization == null || jwtUtil.isExpired(authorization)) {
            log.warn("토큰이 없거나 만료됨");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization;
        String email = jwtUtil.getEmail(token);
        String role = jwtUtil.getRole(token);

        if (email != null && role != null) {
            PermissionRole permissionRole = PermissionRole.valueOf(role);
            Permission permission = permissionManageRepository.findByPermission(permissionRole);

            Members members = Members.builder()
                    .email(email)
                    .permission(permission)
                    .build();

            CustomUserDetails customUserDetails = new CustomUserDetails(members);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info("인증 성공 - 사용자 설정 완료: {}", email);
        }

        filterChain.doFilter(request, response);
    }
}
