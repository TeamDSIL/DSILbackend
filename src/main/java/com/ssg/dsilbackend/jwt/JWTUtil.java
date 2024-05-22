package com.ssg.dsilbackend.jwt;

import com.ssg.dsilbackend.domain.Refresh;
import com.ssg.dsilbackend.oAuth2.CustomOAuth2User;
import com.ssg.dsilbackend.repository.RefreshRepository;
import com.ssg.dsilbackend.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
@Log4j2
public class JWTUtil {
    private final SecretKey secretKey;
    private final RefreshRepository refreshRepository;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret, RefreshRepository refreshRepository) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.refreshRepository = refreshRepository;
    }

    public String getEmail(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String getCategory(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    //    public Boolean isExpired(String token) {
//        try {
//            Date expiration = Jwts.parser().verifyWith(secretKey).build().parseClaimsJws(token).getBody().getExpiration();
//            log.debug("Token expiration time: {}", expiration);
//            return expiration.before(new Date());
//        } catch (Exception e) {
//            log.error("Error parsing JWT token: ", e);
//            return true; // 토큰이 유효하지 않으면 만료된 것으로 간주
//        }
//    }
    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    private String createToken(Map<String, Object> claims, Long expiredMs) {

        var jwtBuilder = Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey);
        // 개별 클레임 추가
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            jwtBuilder.claim(entry.getKey(), entry.getValue());
        }
        String token = jwtBuilder.compact();
        log.debug("Generated JWT token: {}", token);
        return token;
    }

    public void createJWT(HttpServletResponse response, Authentication authentication) {

        String name;
        String email;
        String role;

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            name = userDetails.getUsername();
            email = userDetails.getEmail();
            role = userDetails.getAuthorities().iterator().next().getAuthority();
        } else if (authentication.getPrincipal() instanceof CustomOAuth2User oAuth2User) {
            name = oAuth2User.getName();
            email = oAuth2User.getEmail();
            role = oAuth2User.getAuthorities().iterator().next().getAuthority();
        } else {
            throw new IllegalArgumentException("Unknown principal type: " + authentication.getPrincipal().getClass().getName());
        }


        Map<String, Object> accessClaims = new HashMap<>();
        accessClaims.put("email", email);
        accessClaims.put("category", "access");
        accessClaims.put("role", role);
        accessClaims.put("name", name);

        Map<String, Object> refreshClaims = new HashMap<>();
        refreshClaims.put("email", email);
        refreshClaims.put("category", "refresh");
        refreshClaims.put("role", role);
        accessClaims.put("name", name);

        String accessToken = createToken(accessClaims, 600000L);
        String refreshToken = createToken(refreshClaims, 86400000L);

        addRefreshEntity(email, refreshToken);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(createCookie("refreshToken", refreshToken));

        log.debug("Access Token: {}", accessToken);
        log.debug("Refresh Token: {}", refreshToken);
    }

    private void addRefreshEntity(String email, String refresh) {
        Date date = new Date(System.currentTimeMillis() + 86400000L);

        Refresh refreshOb = Refresh.builder()
                .email(email)
                .refresh(refresh)
                .expiration(date.toString())
                .build();

        refreshRepository.save(refreshOb);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);  // 1일
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }
}