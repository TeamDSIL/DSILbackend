package com.ssg.dsilbackend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.dsilbackend.domain.*;
import com.ssg.dsilbackend.dto.AvailableTimeTable;
import com.ssg.dsilbackend.dto.Crowd;
import com.ssg.dsilbackend.dto.File.FileDTO;
import com.ssg.dsilbackend.dto.restaurantManage.*;
import com.ssg.dsilbackend.service.FileService;
import com.ssg.dsilbackend.service.RestaurantManageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
@Log4j2
public class RestaurantManageController {
    private final RestaurantManageService restaurantManageService;
    private final FileService fileService;


    //식당id로 하나의 식당을 조회하는 메소드
    @GetMapping("/{restaurant-id}")
    public ResponseEntity<RestaurantManageDTO> getRestaurant(@PathVariable("restaurant-id") Long restaurantId) {
        RestaurantManageDTO restaurantDTO = restaurantManageService.getRestaurant(restaurantId);
        if (restaurantDTO == null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(restaurantDTO);
    }

    //식당정보를 수정하는 메소드

    //이제 됩니다. 여기가 기준이오.
    //또 안되니까 수정했다! 이제 컨트롤z그만..
    //이제... 괜찮다고.. 건들지 마라고... 그만
    //그만건들자고!
    //이제 또 분수령이다. 현재시각 5월17일 오후 6시 24분. 간다.
    //또 무언가를 한다. 왜냐면 메뉴추가가 안되거든. 메뉴이미지는 성공했는데. 메뉴추가가 안되서 해보는 수정작업!
    //신중하게 합시다 현재시각 5월 18일 오후 2시 33분......
    @PutMapping("/{restaurant-id}")
    public ResponseEntity<RestaurantManageDTO> updateRestaurant(
            @PathVariable("restaurant-id") Long id,
            @RequestPart("restaurantData") String restaurantDataJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestParam Map<String, MultipartFile> menuImages) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        RestaurantManageDTO restaurantDTO = mapper.readValue(restaurantDataJson, RestaurantManageDTO.class);
        // JSON 데이터 로그 출력
        log.info("제이슨Received restaurantDataJson: {}", restaurantDataJson);



        // 기존 식당 정보 가져오기
        RestaurantManageDTO existingRestaurant = restaurantManageService.getRestaurant(id);

        // 식당 이미지 처리
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileService.uploadFiles(List.of(image), "restaurant-images").get(0).getUploadFileUrl();
            restaurantDTO.setImg(imageUrl); // S3에서 반환된 이미지 URL을 설정
        } else if (existingRestaurant != null && existingRestaurant.getImg() != null) {
            restaurantDTO.setImg(existingRestaurant.getImg());
        }

        // 메뉴 이미지 처리
        // 메뉴를 추가했을 경우에 아이디 때문에 문제생기는걸 해결하고자,,,, 건드려보았던 수리작업
        menuImages.forEach((key, file) -> {
            if (file != null && !file.isEmpty()) {
                String menuName = key.replace("menuImages[", "").replace("]", "");  // key에서 메뉴 이름 추출
                Optional<MenuDTO> menu = restaurantDTO.getMenus().stream()
                        .filter(m -> m.getName().equals(menuName))  // 메뉴 이름으로 필터링
                        .findFirst();
                if (menu.isPresent()) {
                    String menuImageUrl = fileService.uploadFiles(List.of(file), "menu-images").get(0).getUploadFileUrl();
                    menu.get().setImg(menuImageUrl);
                } else {
                    // 해당 이름의 메뉴가 없는 경우 로그 출력 또는 다른 처리
                    log.warn("No menu found with the name: {}", menuName);
                }
            }
        });

        // 기존 메뉴 ID 설정
        if (existingRestaurant != null && existingRestaurant.getMenus() != null) {
            for (int i = 0; i < restaurantDTO.getMenus().size(); i++) {
                if (i < existingRestaurant.getMenus().size()) {
                    MenuDTO existingMenu = existingRestaurant.getMenus().get(i);
                    restaurantDTO.getMenus().get(i).setId(existingMenu.getId());
                }
            }
        }
        log.info("최후의 식당디티오"+restaurantDTO);
        RestaurantManageDTO newRestaurant = restaurantManageService.updateRestaurant(id, restaurantDTO);
        log.info("최후의최후의 식당디티오"+newRestaurant);
//        restaurantDTO.setId(id);
        return ResponseEntity.ok(newRestaurant);

    }



    //멤버id로 해당하는 식당 리스트를 조회하는 메소드
    @GetMapping("/{memberId}/restaurants")
    public ResponseEntity<List<RestaurantManageDTO>> getRestaurantsByMember(@PathVariable Long memberId) {
        List<RestaurantManageDTO> restaurants = restaurantManageService.getRestaurantList(memberId);
        if (restaurants.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(restaurants);
    }

    //식당 혼잡도를 변경하는 메소드
    @PatchMapping("/{restaurantId}/crowd")
    public ResponseEntity<RestaurantManageDTO> updateCrowd(@PathVariable("restaurantId") Long restaurantId, @RequestParam("status") Crowd crowd) {
        try {
            RestaurantManageDTO updatedRestaurantDTO = restaurantManageService.updateCrowd(restaurantId, crowd);
            System.out.println("혼잡도변경메소드의 restaurantDTO는 바로"+updatedRestaurantDTO);
            return ResponseEntity.ok(updatedRestaurantDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //식당id에 해당하는 예약목록을 리턴하는 메소드
    @GetMapping("/{restaurant-id}/reservations")
    public ResponseEntity<List<ReservationDTO>> getReservationList(@PathVariable("restaurant-id") Long restaurantId) {
        List<ReservationDTO> reservations = restaurantManageService.getReservationList(restaurantId);
        if (reservations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reservations);
    }
    //식당에 해당하는 리뷰 목록을 불러오는 메소드
    @GetMapping("/{restaurant-id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewList(@PathVariable("restaurant-id") Long restaurantId) {
        try {
            log.info("리뷰 뽑는 restaurant ID: " + restaurantId);
            List<ReviewDTO> reviews = restaurantManageService.getReviewList(restaurantId);
//            log.info("찾은 리뷰 목록: "+reviews);
            if (reviews.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            log.error("Error while fetching review list for restaurant ID: {}", restaurantId, e);
            return ResponseEntity.status(500).body(null);
        }
    }

    //예약에 해당하는 review를 찾는 메소드
//    @GetMapping("/{reservation-id}/review")
//    public ResponseEntity<ReviewDTO> getReview(@PathVariable Reservation reservation) {
//        ReviewDTO reviewDTO = restaurantManageService.getReview(reservation);
//        if (reviewDTO.isDeleteStatus()) {
//            return ResponseEntity.noContent().build();
//        }
//        return  ResponseEntity.ok(reviewDTO);
//    }

    //해당 리뷰에 답글을 작성하는 메소드
    @PostMapping("/reviews/{review-id}")
    public ResponseEntity<ReplyDTO> createReply(@PathVariable("review-id") Long reviewId, @RequestBody Map<String, String> request) {
        try {
            String content = request.get("content");
            ReplyDTO replyDTO = restaurantManageService.createReply(reviewId, content);
            return ResponseEntity.ok(replyDTO);
        } catch (Exception e) {
            log.error("Error while creating reply for review ID: {}", reviewId, e);
            return ResponseEntity.status(500).body(null);
        }
    }


    // 새로운 AvailableTime 인스턴스를 생성하고 해당 예약가능시간DTO를 리턴하는 메소드
    @PostMapping("/{restaurant-id}/available-times")
    public ResponseEntity<AvailableTimeDTO> createAvailableTime(@PathVariable("restaurant-id") Long restaurantId, @RequestParam AvailableTimeTable slot) {
        AvailableTimeDTO newAvailableTimeDTO = restaurantManageService.createAvailableTime(restaurantId, slot);
        return ResponseEntity.ok(newAvailableTimeDTO);
    }

    // 지정된 AvailableTime 인스턴스를 삭제하는 메소드
    @DeleteMapping("/{restaurant-id}/available-times")
    public ResponseEntity<Void> deleteAvailableTime(@PathVariable("restaurant-id") Long restaurantId, @RequestParam AvailableTimeTable slot) {
        restaurantManageService.deleteAvailableTime(restaurantId, slot);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{restaurant-id}/available-times")
    public ResponseEntity<List<AvailableTimeDTO>> getAvailableTimes(@PathVariable("restaurant-id") Long id) {
        List<AvailableTimeDTO> availableTimes = restaurantManageService.getAvailableTimes(id);
        return ResponseEntity.ok(availableTimes);
    }


    // 리뷰 삭제요청시 deleteStatus를 true로!
    @PatchMapping("/reviews/{review-id}/delete-status")
    public ResponseEntity<ReviewDTO> updateReviewDeleteStatus(@PathVariable("review-id") Long reviewId, @RequestParam boolean deleteStatus) {
        try {
            ReviewDTO reviewDTO = restaurantManageService.updateReviewDeleteStatus(reviewId, deleteStatus);
            return ResponseEntity.ok(reviewDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    //식당수정창에서 카테고리들을 불러올 메소드
    @GetMapping("/{restaurant-id}/categories")
    public ResponseEntity<List<CategoryDTO>> getCategories(@PathVariable("restaurant-id") Long restaurantId){
        try {
            List<CategoryDTO> categoryDTOS = restaurantManageService.getCategoryList(restaurantId);
            System.out.println(categoryDTOS+"여기서도 확인");
            return ResponseEntity.ok(categoryDTOS);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //식당수정창에서 편의시설들을 불러올 메소드
    @GetMapping("/{restaurant-id}/facilities")
    public ResponseEntity<List<FacilityDTO>> getFacilities(@PathVariable("restaurant-id") Long restaurantId){
        try {
            List<FacilityDTO> facilityDTOS = restaurantManageService.getFacilityList(restaurantId);
            return ResponseEntity.ok(facilityDTOS);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //식당수정창에서 메뉴들을 불러올 메소드
    @GetMapping("/{restaurant-id}/menus")
    public ResponseEntity<List<MenuDTO>> getMenus(@PathVariable("restaurant-id") Long restaurantId){
        try {
            List<MenuDTO> menuDTOS = restaurantManageService.getMenuList(restaurantId);
            return ResponseEntity.ok(menuDTOS);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    //리뷰에 자동으로 답글달기
//    @PostMapping("/{reviewId}/reply")
//    public Review addReply(@PathVariable Long reviewId) {
//        return restaurantManageService.addReplyToReview(reviewId);
//    }

    }






