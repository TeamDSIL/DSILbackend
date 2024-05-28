package com.ssg.dsilbackend.controller;


import com.ssg.dsilbackend.dto.File.FileDTO;
import com.ssg.dsilbackend.dto.userManage.*;
import com.ssg.dsilbackend.jwt.JWTUtil;
import com.ssg.dsilbackend.service.FileService;
import com.ssg.dsilbackend.service.TempCodeService;
import com.ssg.dsilbackend.service.UserManageService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/memberManage")
@Log4j2
@RequiredArgsConstructor
public class UserManageController {

    private final UserManageService userManageService;
    private final FileService fileService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final TempCodeService tempCodeService;


    // ------------------------------------------------- login

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    log.info("Found refresh token in cookie: {}", cookie.getValue());
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        } else {
            log.warn("No cookies found in the request");
        }
        response.setStatus(HttpServletResponse.SC_OK);

    }


    @GetMapping("/loginPage")
    public void getLogin() {
    }

    // 로그인 - post 요청
    @PostMapping("/loginPage")
    public ResponseEntity<?> postLogin(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        log.info("도착했는지 확인");
        log.info(loginDTO);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            jwtUtil.createJWT(response, authentication);

            log.info("JWT 토큰 생성 및 설정 완료");
            return ResponseEntity.ok().body("로그인 성공");
        } catch (AuthenticationException e) {
            log.error("로그인 실패", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }

    // ------------------------------------------------- signup

    @GetMapping("/signupPage")
    public void getSignup() {

    }

    // 회원 가입 - post 요청
    @PostMapping("/signupPage")
    public void postSignup(@RequestBody UserManageDTO userManageDTO) {
        log.info(userManageDTO);
        log.info("회원가입 post 요청");
        userManageService.signUp(userManageDTO);
    }


    // ------------------------------------------------- user
    @PostMapping("/findEmail")
    public ResponseEntity<String> findEmail(@RequestBody Map<String, String> payload) {
        String tel = payload.get("tel");
        log.info(tel);

        try {
            String email = userManageService.findEmailByTel(tel);
            if (email != null) {
                return ResponseEntity.ok(email);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("이메일을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    @PostMapping("/checkEmail")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Boolean exists = userManageService.checkEmail(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sendCode")
    public ResponseEntity<String> sendCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        log.info("Received email: {}", email);
        try {
            tempCodeService.sendEmailWithCode(email);
            return ResponseEntity.ok("인증 코드가 전송되었습니다.");
        } catch (MessagingException e) {
            log.error("MessagingException occurred while sending email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송 중 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("Exception occurred while processing request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증 코드를 전송하는 중 오류가 발생했습니다.");
        }
    }

    // 인증 코드 검증
    @PostMapping("/verifyCode")
    public ResponseEntity<String> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        if (tempCodeService.verifyCode(email, code)) {
            return ResponseEntity.ok("인증 되었습니다.");
        } else {
            return ResponseEntity.status(400).body("인증 코드가 틀렸습니다. 다시 시도해주세요.");
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        log.info(email + "비밀번호 설정을 위한 이메일 받기 넘어옴?");
        log.info(newPassword);

        try {
            userManageService.updatePassword(email, newPassword);
            return ResponseEntity.ok("비밀번호가 재설정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("비밀번호를 재설정하는 중 오류가 발생했습니다.");
        }
    }
    // ------------------------------------------------- user

    @GetMapping("/userMyPage")
    public UserManageDTO getUserData(@RequestParam String email) {
        log.info("{} get 요청", email);
        return userManageService.getUserInfoByEmail(email);
    }

    @PostMapping("/userMyPage")
    public void postUserData(@RequestBody UserManageDTO userManageDTO) {
        log.info("{} post 요청", userManageDTO);
        userManageService.modifyUserInfo(userManageDTO);
    }

    @DeleteMapping("/userMyPage")
    public void deleteUserData(@RequestParam String email) {
        log.info("{} delete 요청", email);
        userManageService.deleteUserInfo(email);
    }


    // ------------------------------------------------- owner

    @GetMapping("/ownerMyPage")
    public List<OwnerManageDTO> getOwnerData(@RequestParam String email) {
        log.info(email);
        return userManageService.getRestaurantByEmail(email);
    }


    @PostMapping("/ownerMyPage")
    public void postOwnerData(@RequestBody OwnerManageDTO ownerManageDTO) {
        log.info(ownerManageDTO);
        userManageService.modifyOwnerData(ownerManageDTO);
    }

    // ------------------------------------------------- admin

    // 회원 관리 페이지
    @GetMapping("/adminManageUserPage")
    public List<UserManageDTO> getUserManageList() {
        log.info("일반회원 목록 출력");
        return userManageService.getUserInfoList();
    }

    // 회원 관리 페이지 - 수정
    @PostMapping("/adminManageUserPage")
    public void modifyUserInfo(@RequestBody UserManageDTO userManageDTO) {
        log.info("일반회원 정보 수정");
        log.info(userManageDTO);
        userManageService.modifyUserInfo(userManageDTO);
    }

    // 회원 관리 페이지 - status 값 수정
    @DeleteMapping("/adminManageUserPage")
    public void deleteUserInfo(@RequestParam String email) {
        log.info("일반 회원 정보 삭제");
        log.info(email);
        userManageService.deleteUserInfo(email);

    }

    // 식당 관리자 페이지
    @GetMapping("/adminManageRestaurantPage")
    public List<OwnerManageDTO> getOwnerManageList() {
        log.info("식당 관리자 목록 출력");
        return userManageService.getOwnerInfoList();
    }

    // 식당 관리자 페이지 - 수정
    @PostMapping("/adminManageRestaurantPage")
    public void modifyOwnerInfo(@RequestBody OwnerManageDTO request) {
        log.info("식당 관리자 정보 수정");
        log.info(request);
        userManageService.modifyOwnerInfo(request);
    }

    // 식당 관리자 페이지 - 식당 삭제
    @DeleteMapping("/adminManageRestaurantPage")
    @Transactional
    public void deleteOwnerInfo(@RequestParam String restaurantName) {
        log.info("식당 관리자 정보 삭제");
        log.info(restaurantName);
        userManageService.removeRestaurantByName(restaurantName);
    }

    // 식당등록
    @PostMapping("/registerRestaurant")
    public ResponseEntity<?> registerRestaurant(
            @ModelAttribute RestaurantRegisterDTO restaurantRegisterDTO) {
        try {
            System.out.println(restaurantRegisterDTO);
            // 레스토랑 사진
            MultipartFile resImg = restaurantRegisterDTO.getImg();
            List<FileDTO> fileDTOList = fileService.uploadFiles(List.of(resImg), "restaurnat_img");
            restaurantRegisterDTO.setImgUrl(fileDTOList.get(0).getUploadFileUrl());
            System.out.println("setImgUrl: " + restaurantRegisterDTO.getImgUrl());

            // 메뉴사진
            for (int i = 0; i < restaurantRegisterDTO.getMenuDTOs().size(); i++) {
                MultipartFile menuImg = restaurantRegisterDTO.getMenuDTOs().get(i).getImg();
                List<FileDTO> menuFileDTOList = fileService.uploadFiles(List.of(menuImg), "menu_img");
                restaurantRegisterDTO.getMenuDTOs().get(i).setImgUrl(menuFileDTOList.get(0).getUploadFileUrl());
                System.out.println("setImgUrl: " + i + "번 " + restaurantRegisterDTO.getMenuDTOs().get(i).getImgUrl());
            }

            userManageService.registerRestaurantInfo(restaurantRegisterDTO);
            return ResponseEntity.ok("식당 정보가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("식당 정보 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/reviewManage")
    public ResponseEntity<?> getReviewReplyList() {
        try {
            List<ReviewReplyDTO> reviewReplyDTOS = userManageService.getReviewReplyList();
            for (ReviewReplyDTO review : reviewReplyDTOS) {
                System.out.println(review);
            }
            if (reviewReplyDTOS.isEmpty()) {
                // 데이터가 없는 경우 404 Not Found 반환
                return ResponseEntity.notFound().build();
            } else {
                // 데이터가 있는 경우 200 OK와 함께 데이터 반환
                return ResponseEntity.ok(reviewReplyDTOS);
            }
        } catch (Exception e) {
            // 예외 처리의 경우 500 Internal Server Error 반환
            return ResponseEntity.internalServerError().body("Error accessing data ");
        }
    }


    @DeleteMapping("/removeReview")
    public ResponseEntity<?> removeReview(@RequestBody Map<String, Object> payload) {
        try {
            // `reviewId`를 페이로드에서 추출
            String reviewId = String.valueOf(payload.get("reviewId"));
            System.out.println(reviewId + " 삭제할 리뷰 ID");

            userManageService.removeReview(Long.parseLong(reviewId));

            return ResponseEntity.ok().body("댓글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            System.out.println("댓글 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body("댓글 삭제에 실패했습니다.");
        }
    }

    @DeleteMapping("/removeReply")
    public ResponseEntity<?> removeReply(@RequestBody Map<String, Object> payload) {
        try {
            // `reviewId`를 페이로드에서 추출
            String reviewId = String.valueOf(payload.get("reviewId"));
            System.out.println(reviewId + " 삭제할 댓글을 가진 리뷰 ID");

            userManageService.removeReply(Long.parseLong(reviewId));

            return ResponseEntity.ok().body("댓글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            System.out.println("댓글 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body("댓글 삭제에 실패했습니다.");
        }
    }
}
