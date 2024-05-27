package com.ssg.dsilbackend.dto.restaurantManage;

import com.ssg.dsilbackend.domain.Reply;
import com.ssg.dsilbackend.domain.Reservation;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReviewDTO {
    private Long id;
    private Long replyId;
    private String replyContent;

    private Long reservationId;

    private String content;

    private LocalDate registerDate;

    private Long score;

    private boolean deleteStatus;

    private String img;
    private String reservationName;
}
//reply가 null인 경우에도 대처할 수 있게 수정하였다