package com.ssg.dsilbackend.controller;

import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.dto.userManage.UserManageDTO;
import com.ssg.dsilbackend.jwt.JWTUtil;
import com.ssg.dsilbackend.repository.UserManageRepository;
import com.ssg.dsilbackend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/userInfo")
@RequiredArgsConstructor
@Log4j2
public class TokenController {
    private final UserManageRepository userManageRepository;
    private final JWTUtil jwtUtil;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {

        log.info(authentication);

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Principal 객체의 각 필드 값을 개별적으로 로그 출력
        log.info("Username: " + userDetails.getUsername());
        log.info("Email: " + userDetails.getEmail());


        // 권한 정보 로그 추가
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .reduce((authority1, authority2) -> authority1 + ", " + authority2)
                .orElse("No authorities");
        log.info("Authorities: {}", authorities);

        // 이메일 정보 로그 추가
        String email = userDetails.getEmail();

        Optional<Members> userData = userManageRepository.findByEmail(email);

        if (!userData.isPresent()) {
            log.error("User not found for email: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Members member = userData.orElseThrow(() -> new RuntimeException("User not found"));

        log.info(member);


        UserManageDTO userDTO = UserManageDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .tel(member.getTel())
                .address(member.getAddress())
                .postcode(member.getPostcode())
                .point(member.getPoint())
                .permission(member.getPermission())
                .build();

        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || jwtUtil.isExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtUtil.getEmail(refreshToken);

        Optional<Members> userData = userManageRepository.findByEmail(email);
        if (!userData.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String newAccessToken = jwtUtil.createAccessToken(authentication);

        return ResponseEntity.ok().header("Authorization", "Bearer " + newAccessToken).body(newAccessToken);
    }
}
