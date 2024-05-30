package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Reply;
import com.ssg.dsilbackend.domain.Review;
import com.ssg.dsilbackend.dto.userManage.ReviewReplyDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewManageRepository extends JpaRepository<Review, Long> {

    List<Review> getReviewByDeleteStatus(Boolean deleteStatus);
    @Query("SELECT new com.ssg.dsilbackend.dto.userManage.ReviewReplyDTO(m.email, m.id, r.content, r.id, r.deleteStatus, r.registerDate, rp.content, rp.id, rp.deleteStatus) " +
            "FROM Review r " +
            "JOIN r.reservation rs " +
            "LEFT JOIN r.reply rp " + // 수정된 부분: LEFT JOIN 사용
            "JOIN rs.members m " +
            "WHERE r.deleteStatus = TRUE")
    List<ReviewReplyDTO> findReviewDetails();



}
