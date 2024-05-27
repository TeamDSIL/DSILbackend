package com.ssg.dsilbackend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ssg.dsilbackend.dto.FacilityName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "restaurant_facility")
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facilities_id")
    private Long id;

    @Column(name = "facilities_name", length = 100, nullable = false)
    @Enumerated(EnumType.STRING)
    private FacilityName name;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonBackReference
    private Restaurant restaurant;

    public void setFacilityName(FacilityName name){
        this.name = name;
    }
}
