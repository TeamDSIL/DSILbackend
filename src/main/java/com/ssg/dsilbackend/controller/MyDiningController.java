package com.ssg.dsilbackend.controller;

import com.ssg.dsilbackend.dto.File.FileDTO;
import com.ssg.dsilbackend.dto.myDinig.*;
import com.ssg.dsilbackend.dto.userManage.RestaurantRegisterDTO;
import com.ssg.dsilbackend.service.FileService;
import com.ssg.dsilbackend.service.MyDiningService;
import com.ssg.dsilbackend.service.ReserveService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/myDining")
@Log4j2
@RequiredArgsConstructor
public class MyDiningController {
    private final MyDiningService myDiningService;
    private final FileService fileService;
    private final ReserveService reserveService;

    // 사용자 id 를 받아서 해당 예약리스트 출력
    @GetMapping("/reservations/{id}")
    public ResponseEntity<?> getMydiningListById(@PathVariable Long id) {
        try {
            List<MydiningReserveDTO> reservations = myDiningService.getMydiningReserveListById(id);
            if (reservations.isEmpty()) {
                // 데이터가 없는 경우 404 Not Found 반환
                return ResponseEntity.notFound().build();
            } else {
                // 데이터가 있는 경우 200 OK와 함께 데이터 반환
                return ResponseEntity.ok(reservations);
            }
        } catch (Exception e) {
            // 예외 처리의 경우 500 Internal Server Error 반환
            return ResponseEntity.internalServerError().body("Error accessing data for ID " + id);
        }
    }

    // 사용자 id 를 받아서 해당 즐겨찾기 출력
    @GetMapping("/bookmarks/{id}")
    public List<MydiningBookmarkDTO> getMydiningBookmarksListById(@PathVariable Long id) {
        return myDiningService.getMydiningBookmarksListById(id);
    }

    // 사용자 id 를 받아서 해당 리뷰 출력
    @GetMapping("/reviews/{id}")
    public List<MydiningReviewsDTO> getMydiningReviewsListById(@PathVariable Long id) {
        return myDiningService.getMydiningReviewsListById(id);
    }


    //리뷰 등록
//    @PostMapping("/registerReview")
//    public ResponseEntity<?> registerReview(@RequestBody ReviewRequest reviewRequest) {
//        try {
//            System.out.println(reviewRequest);
//            myDiningService.registerReview(reviewRequest);
//            return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다.");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error registering review: " + e.getMessage());
//        }
//    }

    @PostMapping("/registerReview")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("reviewContents") String reviewContents,
                                        @RequestParam("reservationId") Long reservationId,
                                        @RequestParam("registerDate") String registerDate,
                                        @RequestParam("reviewScore") Long reviewScore,
                                        @RequestParam("userEmail") String userEmail) {
        try {
            // Save file to S3 and get FileDTO
            List<FileDTO> fileDTOList = fileService.uploadFiles(List.of(file), "reviews");
            String img = fileDTOList.get(0).getUploadFileUrl();

            // Save review details
            ReviewRequest reviewRequest = ReviewRequest.builder()
                    .reviewContents(reviewContents)
                    .reservationId(reservationId)
                    .registerDate(LocalDate.parse(registerDate))
                    .reviewScore(reviewScore)
                    .userEmail(userEmail)
                    .img(img)
                    .build();

            myDiningService.registerReview(reviewRequest);

            return ResponseEntity.ok("File uploaded and review saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }





    // 예약 취소 (CANCLED 상태 변경)
    @PutMapping("/reservation-cancel/{reservationId}")
    @Transactional
    public ResponseEntity<?> cancelReservation(@PathVariable Long reservationId, @RequestBody ReservationUpdateRequest updateRequest) {
        boolean result = myDiningService.cancelReservation(reservationId, updateRequest);

        if (result) {
            return ResponseEntity.ok().body("예약이 성공적으로 취소되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("예약 취소 실패: 해당 예약을 찾을 수 없습니다.");
        }
    }

    // 리뷰 취소 요청
    @PutMapping("/reviewRemoveRequest/{reviewId}")
    @Transactional
    public ResponseEntity<?> removeRequestReview(@PathVariable Long reviewId){
        System.out.println(reviewId+"삭제요청할 아이디!!");
        boolean result = myDiningService.removeRequestReview(reviewId);
        if (result) {
            return ResponseEntity.ok().body("취소 요청이 성공적으로 되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("취소 요청 실패: 해당 취소를 찾을 수 없습니다.");
        }
    }

    // 즐겨찾기 아이디 받아서 즐겨찾기 삭제
    @DeleteMapping("/bookmark-cancel/{id}")
    @Transactional
    public ResponseEntity<?> removeBookmarkById(@PathVariable Long id){
        boolean isDeleted = myDiningService.removeBookmark(id);
        if (isDeleted) {
            return ResponseEntity.ok().body("즐겨찾기가 성공적으로 취소되었습니다.");
        } else {
            return ResponseEntity.notFound().build(); // 존재하지 않는 ID로 삭제 요청 시
        }
    }

    @PostMapping("/registerRestaurant")
    public ResponseEntity<?> registerRestaurant(@RequestBody RestaurantRegisterDTO restaurantRegisterDTO) {
        try {
            System.out.println(restaurantRegisterDTO);
            // 여기에 식당 정보를 저장하는 로직을 추가합니다.
            // 예를 들어, 데이터베이스에 restaurantRegisterDTO를 저장하는 코드를 넣을 수 있습니다.

            // 저장 로직 후, 성공적으로 저장되었다는 가정 하에 응답을 반환합니다.
            return ResponseEntity.ok("식당 정보가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            // 예외 발생 시, 500 Internal Server Error와 함께 오류 메시지를 반환합니다.
            return ResponseEntity.internalServerError().body("식당 정보 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

}
