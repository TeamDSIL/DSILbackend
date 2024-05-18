package com.ssg.dsilbackend.domain;

import com.ssg.dsilbackend.dto.AvailableTimeTable;
import com.ssg.dsilbackend.dto.ReservationStateName;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Members members;

    @Column(name = "reservation_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStateName reservationStateName;


    @Column(name = "people_count", nullable = false)
    private int peopleCount;

    @Column(name = "reservation_time", nullable = false)
    @Enumerated(EnumType.STRING)
    private AvailableTimeTable reservationTime;

    @Column(name = "reservation_name")
    private String reservationName;

    @Column(name = "reservation_request")
    private String requestContent;


    @Column(name = "reservation_date",nullable = false)
    private LocalDate reservationDate;

    @Column(name = "reservation_tel",length = 13)
    private String reservationTel;

    @Column(name = "created_at")
    private LocalDateTime createdTime;

    public void setReservationStateName(ReservationStateName state) {
        this.reservationStateName = state;
    }

}






