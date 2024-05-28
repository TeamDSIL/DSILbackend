package com.ssg.dsilbackend.config;


import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.repository.UserManageRepository;
import com.ssg.dsilbackend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserManageRepository userManageRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        Members member = userManageRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("회원 정보가 없습니다."));

        if (!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            throw new BadCredentialsException("잘못된 비밀번호입니다.");
        }

        if (!member.isStatus()) {
            throw new BadCredentialsException("비활성화된 계정입니다.");
        }

        CustomUserDetails userDetails = new CustomUserDetails(member);
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}