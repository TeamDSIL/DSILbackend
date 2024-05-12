package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.domain.Permission;
import com.ssg.dsilbackend.domain.Point;
import com.ssg.dsilbackend.domain.Restaurant;
import com.ssg.dsilbackend.dto.userManage.RestaurantRegisterDTO;
import com.ssg.dsilbackend.dto.userManage.UserManageDTO;
import com.ssg.dsilbackend.repository.PermissionManageRepository;
import com.ssg.dsilbackend.repository.PointManageRepository;
import com.ssg.dsilbackend.repository.RestaurantManageRepository;
import com.ssg.dsilbackend.repository.UserManageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

@SpringBootTest
@Log4j2

public class UserManageServiceTest {

    private UserManageRepository userManageRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private PointManageRepository pointManageRepository;
    private PermissionManageRepository permissionManageRepository;
    private RestaurantManageRepository restaurantManageRepository;

    private UserManageService userManageService;

    @Autowired
    public UserManageServiceTest(UserManageService userManageService){
        this.userManageService = userManageService;
    }



    @Autowired
    public UserManageServiceTest(UserManageRepository userManageRepository, BCryptPasswordEncoder bCryptPasswordEncoder,PointManageRepository pointManageRepository,PermissionManageRepository permissionManageRepository,RestaurantManageRepository restaurantManageRepository) {
        this.userManageRepository = userManageRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.pointManageRepository = pointManageRepository;
        this.permissionManageRepository = permissionManageRepository;
        this.restaurantManageRepository = restaurantManageRepository;
    }

    @Test
    public void signUpTest(){
//        Point point = new Point().builder()
//                .accumulatePoint(0L)
//                .currentPoint(0L)
//                .build();
//
//        pointManageRepository.save(point);
//
//        Permission permission = new Permission().builder()
//                .name("USER").build();
//
//        permissionManageRepository.save(permission);
//
//        Members data = new Members()
//                .builder()
//                .email("dvbf@naver.com")
//                .password(bCryptPasswordEncoder.encode("1004"))
//                .permission(permission)
//                .name("윤호상")
//                .point(point)
//                .tel("010-9677-7048")
//                .address("서울시 노원구 중계동")
//                .postcode("01709")
//                .build();
//
//        userManageRepository.save(data);
    }

    @Test
    public void getUserInfoByEmailTest(){
        Optional<Members> userData = userManageRepository.findByEmail("user02@example.com");

        Members members = userData.orElseThrow(() -> new RuntimeException("User not found"));
        log.info(members.toString());
    }


    @Test   // # 문제있음
    public void modifyUserInfoTest(){
        Optional<Members> userInfo = userManageRepository.findByEmail("dvbf@naver.com");

        Members members = userInfo.orElseThrow(() -> new RuntimeException("User not found"));

//        Boolean isExist = userManageRepository.existsByEmail(members.getEmail());
//
//        if (isExist) {
//            return;
//        }

        Members userData = members.builder()
                .email(members.getEmail())
                .password(bCryptPasswordEncoder.encode("1000"))
                .name("호호호호")
                .tel(members.getTel())
                .point(members.getPoint())
                .permission(members.getPermission())
                .address(members.getAddress())
                .postcode(members.getPostcode())
                .build();

        log.info(userData.toString());

        userManageRepository.save(userData);
    }

    @Test
    public void deleteUserInfo(){
        Optional<Members> userInfo = userManageRepository.findByEmail("user14@example.com");
        Members members = userInfo.orElseThrow(() -> new RuntimeException("User not found"));

        members.setMemberState(false);
        log.info(members.toString());
        userManageRepository.save(members);
    }

    @Test
    public void getRestaurantNameByEmail(){
        List<Restaurant> restaurantList = restaurantManageRepository.getRestaurantsByMemberId(15L);

        log.info("Restaurant names: {}", () ->
                restaurantList.stream()
                        .map(restaurant -> restaurant.getName())
                        .collect(Collectors.joining(", ")));

    }

    @Test
    public void modifyOwnerInfoTest(){

    }


}
