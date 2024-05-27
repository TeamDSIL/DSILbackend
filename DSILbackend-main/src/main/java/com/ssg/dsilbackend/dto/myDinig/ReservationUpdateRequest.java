package com.ssg.dsilbackend.dto.myDinig;

import com.ssg.dsilbackend.dto.ReservationStateName; // 필요한 enum import

public class ReservationUpdateRequest {
    private ReservationStateName reservationState; // enum 타입으로 변경

    public ReservationStateName getReservationState() {
        return reservationState;
    }

    public void setReservationState(ReservationStateName reservationState) {
        this.reservationState = reservationState;
    }
}