package com.ssg.dsilbackend.dto.restaurantManage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvailableTimeDTO {
    private Long id;
    private String availableTime; // Enum 값은 문자열로 전환

    // 기본 생성자
    public AvailableTimeDTO() {}

    // 인자를 받는 생성자
    public AvailableTimeDTO(Long id, String availableTime) {
        this.id = id;
        this.availableTime = availableTime;
    }

    // Getter와 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(String availableTime) {
        this.availableTime = availableTime;
    }
}