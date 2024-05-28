package com.ssg.dsilbackend.security;

import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.repository.UserManageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserManageRepository userManageRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //DB에서 조회
        Optional<Members> userData = userManageRepository.findByEmail(email);

        // 사용자를 찾지 못한 경우에는 예외를 던집니다.
        Members members = userData.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        log.info(members);

        if (!members.isStatus()) {
            throw new UsernameNotFoundException("해당 계정은 비활성화 상태입니다.");
        }

        // Optional 을 벗겨서 CustomUserDetails 객체로 변환하여 반환합니다.
        return new CustomUserDetails(members);
    }
}
