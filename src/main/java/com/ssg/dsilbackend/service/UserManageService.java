package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.dto.userManage.*;

import java.util.List;

public interface UserManageService {

    void signUp(UserManageDTO userManageDTO);
    UserManageDTO getUserInfoByEmail(String email);
    void modifyUserInfo(UserManageDTO userManageDTO);
    void deleteUserInfo(String email);

    List<OwnerManageDTO> getRestaurantByEmail(String email);
    void modifyOwnerData(OwnerManageDTO ownerManageDTO);

    List<UserManageDTO> getUserInfoList();
    List<OwnerManageDTO> getOwnerInfoList();


    List<ReviewReplyDTO> getReviewReplyList();

    void modifyOwnerInfo(OwnerManageDTO ownerManageDTO);
    void removeRestaurantByName(String name);


    void registerRestaurantInfo(RestaurantRegisterDTO dto);
    void removeReview(Long reviewId);

    void removeReply(Long replyId);




}
