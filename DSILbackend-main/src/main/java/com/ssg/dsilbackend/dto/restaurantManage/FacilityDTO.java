package com.ssg.dsilbackend.dto.restaurantManage;

import com.ssg.dsilbackend.dto.FacilityName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityDTO {
    private Long id;
    private FacilityName name;
    private Long restaurantId;
}
