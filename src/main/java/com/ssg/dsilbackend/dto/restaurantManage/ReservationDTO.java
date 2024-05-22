package com.ssg.dsilbackend.dto.restaurantManage;

import com.ssg.dsilbackend.dto.AvailableTimeTable;
import com.ssg.dsilbackend.dto.ReservationStateName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReservationDTO {
    private Long id;
    private Long restaurantId;
    private Long memberId;
    private ReservationStateName reservationStateName;
    private int peopleCount;
    private AvailableTimeTable reservationTime;
    private String reservationName;
    private String requestContent;
    private LocalDate reservationDate;
    private String reservationTel;
}
