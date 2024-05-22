package com.ssg.dsilbackend.domain;

import com.ssg.dsilbackend.dto.AvailableTimeTable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "available_time")
public class AvailableTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "available_id")
    private Long id;

    @Column(name = "available")
    @Enumerated(EnumType.STRING)
    private AvailableTimeTable availableTime;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    public static String getSlot(Object o) {
        return null;
    }
}
