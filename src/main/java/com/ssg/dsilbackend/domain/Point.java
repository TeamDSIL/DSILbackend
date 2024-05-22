package com.ssg.dsilbackend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "point")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long id;

    @Column(name = "point_accumulate")
    private Long accumulatePoint;

    @Column(name = "point_currunt", nullable = false)
    private Long currentPoint;

    public void setCurrentPoint(Long currentPoint){
        this.currentPoint = currentPoint;
    }
}
