package com.ssg.dsilbackend.dto.reserve;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
public class ReserveDTO {
    private Long reservationId; // 예약 ID 추가
    private Long restaurantId;
    private Long memberId;
    private String reservationStateName;
    private int peopleCount;
    private String reservationTime;
    private String reservationName;
    private String requestContent;
    private LocalDate reservationDate;
    private String reservationTel;
    private LocalDateTime createdTime;
}
