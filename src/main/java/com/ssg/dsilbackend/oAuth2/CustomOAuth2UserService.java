package com.ssg.dsilbackend.oAuth2;

import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.domain.Point;
import com.ssg.dsilbackend.dto.PermissionRole;
import com.ssg.dsilbackend.dto.userManage.UserManageDTO;
import com.ssg.dsilbackend.repository.PermissionManageRepository;
import com.ssg.dsilbackend.repository.UserManageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserManageRepository userManageRepository;
    private final PermissionManageRepository permissionManageRepository;
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

        //추후 작성
        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
//        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();

        String name = oAuth2Response.getName();


        Members existData = userManageRepository.findByName(name);


        Point point = Point.builder()
                .accumulatePoint(0L)
                .currentPoint(0L)
                .build();

        if (existData == null) {
            Members members = Members.builder()
                    .email(oAuth2Response.getEmail())
                    .name(oAuth2Response.getName())
                    .tel("")
                    .status(true)
                    .address("")
                    .postcode("")
                    .registerNumber("")
                    .point(point)
                    .permission(permissionManageRepository.findByPermission(PermissionRole.USER))
                    .build();

            userManageRepository.save(members);
            return createCustomOAuth2User(members);
        } else {
            existData.updateMemberStatus(true);
            userManageRepository.save(existData);
            return createCustomOAuth2User(existData);
        }
    }

    private CustomOAuth2User createCustomOAuth2User(Members members) {
        UserManageDTO userDTO = UserManageDTO.builder()
                .email(members.getEmail())
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
