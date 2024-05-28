package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.domain.*;

import com.ssg.dsilbackend.dto.PermissionRole;
import com.ssg.dsilbackend.dto.userManage.OwnerManageDTO;
import com.ssg.dsilbackend.dto.userManage.RestaurantRegisterDTO;
import com.ssg.dsilbackend.dto.userManage.UserManageDTO;
import com.ssg.dsilbackend.repository.*;
import com.ssg.dsilbackend.dto.CategoryName;
import com.ssg.dsilbackend.dto.Crowd;
import com.ssg.dsilbackend.dto.FacilityName;
import com.ssg.dsilbackend.dto.userManage.*;
import jakarta.transaction.Transactional;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Builder
@Log4j2
public class UserManageServiceImpl implements UserManageService {

    private final UserManageRepository userManageRepository;
    private final ReviewManageRepository reviewManageRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReplyManageRepository replyManageRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RestaurantManageRepository restaurantManageRepository;
    private final MenuRepository menuRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final PointManageRepository pointManageRepository;
    private final PermissionManageRepository permissionManageRepository;
    private final FacilityRepository facilityRepository;


    //    ----------------------------------------------- User


    @Override
    public Boolean checkEmail(String email) {
        return userManageRepository.existsByEmail(email);
    }

    // 회원가입 #테스트 성공
    @Override
    public void signUp(UserManageDTO userManageDTO) {

        String email = userManageDTO.getEmail();

        Boolean isExist = userManageRepository.existsByEmail(email);

        if (isExist) {
            return;
        }

        Point point = Point.builder()
                .accumulatePoint(0L)
                .currentPoint(0L)
                .build();

        pointManageRepository.save(point);

        Permission permission = permissionManageRepository.findByPermission(PermissionRole.USER);

        Members userData = Members
                .builder()
                .email(userManageDTO.getEmail())
                .password(bCryptPasswordEncoder.encode(userManageDTO.getPassword()))
                .name(userManageDTO.getName())
                .tel(userManageDTO.getTel())
                .address(userManageDTO.getAddress())
                .postcode(userManageDTO.getPostcode())
                .point(point)
                .permission(permission)
                .status(true)
                .build();

        log.info(userData);

        userManageRepository.save(userData);
    }

    // 사용자 이메일로 회원 정보 갖고오기 #테스트 성공
    @Override
    public UserManageDTO getUserInfoByEmail(String email) {
        Members members = userManageRepository.findMembersByPermissionRoleAndStatusAndEmail(PermissionRole.USER,true,email);

        UserManageDTO userInfo = UserManageDTO
                .builder()
                .email(members.getEmail())
                .name(members.getName())
                .tel(members.getTel())
                .address(members.getAddress())
                .postcode(members.getPostcode())
                .point(members.getPoint())
                .build();

        return userInfo;
    }

    // 회원 정보 수정 #테스트 성공
    @Override
    public void modifyUserInfo(UserManageDTO userManageDTO) {
        Optional<Members> userInfo = userManageRepository.findByEmail(userManageDTO.getEmail());
        Members members = userInfo.orElseThrow(() -> new RuntimeException("User not found"));

        log.info(members);
        members.updatePassword(bCryptPasswordEncoder.encode(userManageDTO.getPassword()));
        members.updateMemberInfo(userManageDTO);
        log.info(members);
        userManageRepository.save(members);
    }

    // 회원 정보 탈퇴 #테스트 성공
    @Override
    public void deleteUserInfo(String email) {

        Optional<Members> userInfo = userManageRepository.findByEmail(email);
        Members members = userInfo.orElseThrow(() -> new RuntimeException("User not found"));
        members.updateMemberStatus(false);
        userManageRepository.save(members);

    }

    @Override
    public void updatePassword(String email, String newPassword) {
        Optional<Members> user = userManageRepository.findByEmail(email);
        Members member = user.orElseThrow(()->new RuntimeException("User not found"));
        log.info(email+"서비스임플 이메일");
        log.info(newPassword+"서비스임플 비번");
        member.updatePassword(bCryptPasswordEncoder.encode(newPassword));
        userManageRepository.save(member);
    }

    @Override
    public String findEmailByTel(String tel) {
        Optional<Members> userInfo = userManageRepository.findMembersByTel(tel);
        Members members = userInfo.orElseThrow(() -> new RuntimeException("User not found"));
        return members.getEmail();
    }

//    ----------------------------------------------- Owner

    // 하나의 식당 관리자가 여러 식당들을 갖고 있음 -> 식당을 선택할 수 있는 사이드 바
    @Override
    public List<OwnerManageDTO> getRestaurantByEmail(String email) {
        Members member = userManageRepository.findMembersByPermissionRoleAndStatusAndEmail(PermissionRole.OWNER, true,email);
        // 권한이 owner 이고, status 가 1이고, email 이 a@a1인 사장
        List<Restaurant> restaurantList = restaurantRepository.findByMemberId(member.getId());
        // 그 사장의 member_id 를 갖고 있는 매장 목록

        List<OwnerManageDTO> ownerWithRestaurants = new ArrayList<>();
        // ownerManageDTO 에 매장 목록을 담고 그에 맞는 사장 정보 꽂기
        for (Restaurant restaurant: restaurantList){
            OwnerManageDTO ownerManageDTO = OwnerManageDTO.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .password(member.getPassword())
                    .permission(member.getPermission())
                    .ownerName(member.getName())
                    .name(restaurant.getName())
                    .tel(restaurant.getTel())
                    .address(restaurant.getAddress())
                    .registerNumber(member.getRegisterNumber())
                    .build();
            // OwnerManageDTO 를 ownerWithRestaurants 리스트에 추가합니다.
            ownerWithRestaurants.add(ownerManageDTO);
        }

        log.info(ownerWithRestaurants);
        return ownerWithRestaurants;

    }

    @Override
    public void modifyOwnerData(OwnerManageDTO ownerManageDTO) {
        Optional<Members> userInfo = userManageRepository.findByEmail(ownerManageDTO.getEmail());
        Members members = userInfo.orElseThrow(() -> new RuntimeException("User not found"));

        Restaurant restaurantInfo = restaurantRepository.findRestaurantByName(ownerManageDTO.getName());
        restaurantInfo.updateRestaurantInfo(ownerManageDTO);
        members.updatePassword(bCryptPasswordEncoder.encode(ownerManageDTO.getPassword()));
        members.setOwnerPostcode();

        userManageRepository.save(members);
        log.info(members);
        restaurantRepository.save(restaurantInfo);
        log.info(restaurantInfo);
    }


    // 식당 관리자의 회원 정보 수정
    @Override
    public void modifyOwnerInfo(OwnerManageDTO ownerManageDTO) {
        Restaurant restaurantInfo = restaurantRepository.getRestaurantById(ownerManageDTO.getId());
        restaurantInfo.updateRestaurantInfo(ownerManageDTO);

        restaurantRepository.save(restaurantInfo);
        log.info(restaurantInfo);
    }

    //    ----------------------------------------------- Admin

    // 일반회원 목록 # 테스트 성공
    @Override
    public List<UserManageDTO> getUserInfoList() {

        List<Members> userDataList = userManageRepository.findMembersByPermissionRoleAndStatus(PermissionRole.USER, true);

        List<UserManageDTO> userList = userDataList.stream()
                .map(member -> modelMapper.map(member, UserManageDTO.class))
                .collect(Collectors.toList());

        return userList;
    }


    // 식당 목록 # 테스트 성공
    @Override
    public List<OwnerManageDTO> getOwnerInfoList() {
        List<OwnerManageDTO> ownersWithRestaurants = new ArrayList<>();

        List<Members> membersList = userManageRepository.findMembersByPermissionRoleAndStatus(PermissionRole.OWNER, true);
        for (Members member : membersList) {
            List<Restaurant> restaurants = restaurantRepository.findByMemberId(member.getId());
            for (Restaurant restaurant : restaurants) {
                OwnerManageDTO ownerDTO = getOwnerManageDTO(member, restaurant);

                ownersWithRestaurants.add(ownerDTO);
            }
        }
        log.info(ownersWithRestaurants);
        return ownersWithRestaurants;
    }

    private static OwnerManageDTO getOwnerManageDTO(Members member, Restaurant restaurant) {
        OwnerManageDTO ownerDTO = new OwnerManageDTO();
        ownerDTO.setId(restaurant.getId());
        ownerDTO.setEmail(member.getEmail());
        ownerDTO.setName(restaurant.getName()); // 식당의 이름으로 설정
        ownerDTO.setOwnerName(member.getName()); // 회원의 이름으로 설정
        ownerDTO.setPassword(member.getPassword());
        ownerDTO.setPermission(member.getPermission());
        ownerDTO.setTel(restaurant.getTel());
        ownerDTO.setAddress(restaurant.getAddress());
        ownerDTO.setRegisterNumber(member.getRegisterNumber());
        return ownerDTO;
    }

    @Override
    @Transactional
    public void removeRestaurantByName(String name) {
        restaurantRepository.removeByName(name);
    }

    // 등록해줘야 하는 식당
    @Transactional
    public void registerRestaurantInfo(RestaurantRegisterDTO dto) {
        System.out.println("서비스: "+dto);

        // 회원 정보 생성 및 저장
        Permission permission = permissionManageRepository.findByPermission(PermissionRole.OWNER);
        Members newMember = Members.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .name(dto.getUserName())
                .tel(dto.getUserTel())
                .address(dto.getUserAddress())
                .postcode(dto.getPostcode())
                .registerNumber(dto.getRegisterNumber())
                .permission(permission)  // 저장된 Permission 객체 사용
                .status(true)
                .build();
        userManageRepository.save(newMember);

        // 레스토랑 정보 생성 및 저장
        Restaurant newRestaurant = Restaurant.builder()
                .member(newMember)
                .name(dto.getRestaurantName())
                .address(dto.getRestaurantAddress())
                .tel(dto.getRestaurantTel())
                .img(dto.getImgUrl())
                .deposit(dto.getDeposit())
                .tableCount(dto.getTableCount())
                .crowd(Crowd.AVAILABLE)
                .description(dto.getDescription())
                .build();
        restaurantManageRepository.save(newRestaurant);


        // 카테고리 정보 생성 및 저장
        if (dto.getCategories() != null) {
            for (String categoryName : dto.getCategories()) {
                Category category = Category.builder()
                        .name(CategoryName.valueOf(categoryName))
                        .restaurant(newRestaurant)
                        .build();
                categoryRepository.save(category);
            }
        }

        // 메뉴 정보 생성 및 저장
        if (dto.getMenuDTOs() != null) {
            for (RegisterMenuDTO menuDTO : dto.getMenuDTOs()) {
                Menu menu = Menu.builder()
                        .name(menuDTO.getName())
                        .price(menuDTO.getPrice())
                        .img(menuDTO.getImgUrl())
                        .menuInfo(menuDTO.getMenuInfo())
                        .restaurant(newRestaurant)
                        .build();
                menuRepository.save(menu);
            }
        }

        // 편의시설 정보 생성 및 저장
        if (dto.getFacilities() != null) {
            for (String facilityName : dto.getFacilities()) {
                Facility facility = Facility.builder()
                        .name(FacilityName.valueOf(facilityName))
                        .restaurant(newRestaurant)
                        .build();
                facilityRepository.save(facility);
            }
        }
    }

    // 리뷰, 댓글 정보 리스트 가져오기
    @Transactional
    public List<ReviewReplyDTO> getReviewReplyList() {
        List<ReviewReplyDTO> reviewReplyDTOS = reviewManageRepository.findReviewDetails();
        return reviewReplyDTOS;
    }

    @Transactional
    public void removeReview(Long reviewId) {
        System.out.println(reviewId+" 서비스에서 받음");
        Review review = reviewManageRepository.getById(reviewId);
        Reply reply = review.getReply();
        reviewManageRepository.deleteById(reviewId);
        replyManageRepository.delete(reply);
    }

    @Transactional
    public void removeReply(Long reviewId) {
        Review review = reviewManageRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다. ID=" + reviewId));
        Reply reply = review.getReply();
        // Reply 연관관계 제거
        review.setReplyNUll();
        reviewManageRepository.save(review);  // 업데이트된 Review 저장
        replyManageRepository.delete(reply);
    }

}
