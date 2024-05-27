package com.ssg.dsilbackend.dto.userManage;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewReplyDTO {
    String userEamil; //Members의 email
    Long userId; //Members의 id

    String reviewContent; //Review의 content
    Long reviewId; //Review의 id
    Boolean reviewStatus; // Review의 deleteStatus
    LocalDate registerDate; // Review의 registerDate

    String replyContent; // Reply의 content
    Long replyId; // Reply의 id
    Boolean replyStatus; // Reply의 deleteStatus


}
