package com.ssg.dsilbackend.oAuth2;

import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.domain.Point;
import com.ssg.dsilbackend.dto.PermissionRole;
import com.ssg.dsilbackend.dto.userManage.UserManageDTO;
import com.ssg.dsilbackend.repository.PermissionManageRepository;
import com.ssg.dsilbackend.repository.PointManageRepository;
import com.ssg.dsilbackend.repository.UserManageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserManageRepository userManageRepository;
    private final PermissionManageRepository permissionManageRepository;
    private final PointManageRepository pointManageRepository;
//    private final JWTUtil jwtUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {

            return null;
        }

        String email = oAuth2Response.getEmail();
        log.info(email);

        Optional<Members> userData = userManageRepository.findByEmail(email);


        Members members;
        if (userData.isEmpty()) {

            Point point = Point.builder()
                    .accumulatePoint(0L)
                    .currentPoint(0L)
                    .build();

            pointManageRepository.save(point);

            members = Members.builder()
                    .email(oAuth2Response.getEmail())
                    .name(oAuth2Response.getName())
                    .password("")
                    .tel("")
                    .status(true)
                    .address("")
                    .postcode("")
                    .registerNumber("")
                    .point(point)
                    .permission(permissionManageRepository.findByPermission(PermissionRole.USER))
                    .build();

        } else {
            members = userData.get();
            members.updateMemberStatus(true);
        }
        userManageRepository.save(members);
        return createCustomOAuth2User(members, oAuth2User.getAttributes(), oAuth2User.getAuthorities());

    }

    private CustomOAuth2User createCustomOAuth2User(Members members, Map<String, Object> attributes, Collection<? extends GrantedAuthority> authorities) {
        UserManageDTO userDTO = UserManageDTO.builder()
                .email(members.getEmail())
                .password(members.getPassword())
                .name(members.getName())
                .tel(members.getTel())
                .address(members.getAddress())
                .postcode(members.getPostcode())
                .point(members.getPoint())
                .permission(members.getPermission())
                .build();

        return new CustomOAuth2User(userDTO);
    }
}
