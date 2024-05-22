//package com.ssg.dsilbackend.service;
//
//import com.ssg.dsilbackend.domain.Members;
//import com.ssg.dsilbackend.domain.Permission;
//import com.ssg.dsilbackend.domain.Point;
//import com.ssg.dsilbackend.domain.Restaurant;
//import com.ssg.dsilbackend.dto.PermissionRole;
//import com.ssg.dsilbackend.dto.userManage.OwnerManageDTO;
//import com.ssg.dsilbackend.dto.userManage.UserManageDTO;
//import com.ssg.dsilbackend.repository.*;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.Test;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@SpringBootTest
//@Log4j2
//@NoArgsConstructor
//@AllArgsConstructor
//public class UserManageServiceTest {
//
//    private UserManageRepository userManageRepository;
//    private BCryptPasswordEncoder bCryptPasswordEncoder;
//    private PointManageRepository pointManageRepository;
//    private PermissionManageRepository permissionManageRepository;
//    private RestaurantRepository restaurantRepository;
//    private ModelMapper modelMapper;
//    private RestaurantManageRepository restaurantManageRepository;
//
//
//    @Autowired
//    public UserManageServiceTest(UserManageRepository userManageRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
//                                 PointManageRepository pointManageRepository,PermissionManageRepository permissionManageRepository,
//                                 RestaurantRepository restaurantRepository, ModelMapper modelMapper) {
//        this.userManageRepository = userManageRepository;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//        this.pointManageRepository = pointManageRepository;
//        this.permissionManageRepository = permissionManageRepository;
//        this.restaurantRepository = restaurantRepository;
//        this.modelMapper = modelMapper;
//    }
//
//    @Test
//    public void signUpTest(){
//        Point point = Point.builder()
//                .accumulatePoint(0L)
//                .currentPoint(0L)
//                .build();
//
//        pointManageRepository.save(point);
//
//        Permission permission = permissionManageRepository.findByPermission(PermissionRole.USER);
//
//        Members data = new Members()
//                .builder()
//                .email("fbvd@naver.com")
//                .password(bCryptPasswordEncoder.encode("1004"))
//                .permission(permission)
//                .name("윤윤윤")
//                .point(point)
//                .tel("010-1234-1234")
//                .address("서울시 중계 중계")
//                .postcode("01234")
//                .build();
//
//        userManageRepository.save(data);
//    }
//
//    @Test
//    public void getUserInfoByEmailTest(){
//        Optional<Members> userData = userManageRepository.findByEmail("user02@example.com");
//
//        Members members = userData.orElseThrow(() -> new RuntimeException("User not found"));
//        log.info(members.toString());
//    }
//
//
//    @Test
//    public void modifyUserInfoTest(){
//        Optional<Members> userInfo = userManageRepository.findByEmail("dvbf@naver.com");
//
//        Members members = userInfo.orElseThrow(() -> new RuntimeException("User not found"));
//
//        UserManageDTO userManageDTO = UserManageDTO.builder()
//                .email(members.getEmail())
//                .password(members.getPassword())
//                .name("ghgh")
//                .tel("123124")
//                .address("우리집")
//                .postcode("12345")
//                .build();
//
//        members.updateMemberInfo(userManageDTO);
//        log.info(members);
//        userManageRepository.save(members);
//    }
//
//    @Test
//    public void deleteUserInfo(){
//        Optional<Members> userInfo = userManageRepository.findByEmail("dvbf@naver.com");
//        Members members = userInfo.orElseThrow(() -> new RuntimeException("User not found"));
//
//        members.updateMemberStatus(false);
//        log.info(members.toString());
//        userManageRepository.save(members);
//    }
//
//    @Test
//    public void getRestaurantNameByEmailTest(){
//        List<Restaurant> restaurantList = restaurantRepository.findByMemberId(15L);
//
//        log.info("Restaurant names: {}", () ->
//                restaurantList.stream()
//                        .map(restaurant -> restaurant.getName())
//                        .collect(Collectors.joining(", ")));
//    }
//
//    @Test   // # 추후에 해결
//    public void modifyOwnerInfoTest(){
//
//        OwnerManageDTO ownerManageDTO = OwnerManageDTO.builder()
//                .id(1L)
//                .email("a@a1")
//                .password("1111")
//                .ownerName("김사장님1")
//                .address("123 서울시 서울구 서울동22")
//                .name("서울식당")
//                .registerNumber("607-66-12034")
//                .tel("12345565")
//                .build();
//
//        Optional<Members> userInfo = userManageRepository.findByEmail(ownerManageDTO.getEmail());
//        Members members = userInfo.orElseThrow(() -> new RuntimeException("User not found"));
//
//        Restaurant restaurantInfo = restaurantRepository.getRestaurantById(ownerManageDTO.getId());
//        restaurantInfo.updateRestaurantInfo(ownerManageDTO);
//        members.updatePassword(bCryptPasswordEncoder.encode(ownerManageDTO.getPassword()));
//
//
//
//
//        userManageRepository.save(members);
//        log.info(members);
//        restaurantRepository.save(restaurantInfo);
//        log.info(restaurantInfo);
//        log.info("여기까지 왔니");
//    }
//
//    @Test
//    public void getUserInfoListTest(){
//        List<Members> userDataList = userManageRepository.findMembersByPermissionRoleAndStatus(PermissionRole.USER, true);
//
//        List<UserManageDTO> userList = userDataList.stream()
//                .map(member -> modelMapper.map(member, UserManageDTO.class))
//                .toList();
//
//        log.info("User List: {}", userList.stream()
//                .map(UserManageDTO::toString)
//                .toList());
//    }
//
//    // 식당에 대한 목록을 먼저 dto 에 담고
//    // 식당 엔티티의 Member member의 id와 member의 id가 매핑되서 dto에 들어가야됨
//
//    @Test
//    public void getOwnerInfoListTest(){
//        List<OwnerManageDTO> ownersWithRestaurants = new ArrayList<>();
//
//        List<Members> membersList = userManageRepository.findMembersByPermissionRoleAndStatus(PermissionRole.OWNER, true);
//        for (Members member : membersList) {
//            List<Restaurant> restaurants = restaurantRepository.findByMemberId(member.getId());
//            for (Restaurant restaurant : restaurants) {
//                OwnerManageDTO ownerDTO = new OwnerManageDTO();
//                ownerDTO.setId(member.getId());
//                ownerDTO.setEmail(member.getEmail());
//                ownerDTO.setName(restaurant.getName()); // 식당의 이름으로 설정
//                ownerDTO.setOwnerName(member.getName()); // 회원의 이름으로 설정
//                ownerDTO.setPassword(member.getPassword());
//                ownerDTO.setPermission(member.getPermission());
//                ownerDTO.setTel(member.getTel());
//                ownerDTO.setAddress(member.getAddress());
//                ownerDTO.setRegisterNumber(member.getRegisterNumber());
//
//                ownersWithRestaurants.add(ownerDTO);
//            }
//        }
//        log.info(ownersWithRestaurants);
//    }
//
//    @Test
//    public void getRestaurantByEmailTest(){
//
//        Members member = userManageRepository.findMembersByPermissionRoleAndStatusAndEmail(PermissionRole.OWNER, true,"a@a1");
//        // 권한이 owner 이고, status 가 1이고, email 이 a@a1인 사장
//        List<Restaurant> restaurantList = restaurantRepository.findByMemberId(member.getId());
//        // 그 사장의 member_id 를 갖고 있는 매장 목록
//
//        List<OwnerManageDTO> ownerWithRestaurants = new ArrayList<>();
//        // ownerManageDTO 에 매장 목록을 담고 그에 맞는 사장 정보 꽂기
//        for (Restaurant restaurant: restaurantList){
//                OwnerManageDTO ownerManageDTO = OwnerManageDTO.builder()
//                        .id(member.getId())
//                        .email(member.getEmail())
//                        .password(member.getPassword())
//                        .permission(member.getPermission())
//                        .ownerName(member.getName())
//                        .name(restaurant.getName())
//                        .tel(restaurant.getTel())
//                        .address(restaurant.getAddress())
//                        .registerNumber(member.getRegisterNumber())
//                        .build();
//                // OwnerManageDTO 를 ownerWithRestaurants 리스트에 추가합니다.
//                ownerWithRestaurants.add(ownerManageDTO);
//        }
//
//        log.info(ownerWithRestaurants);
//    }
//
//
//}
