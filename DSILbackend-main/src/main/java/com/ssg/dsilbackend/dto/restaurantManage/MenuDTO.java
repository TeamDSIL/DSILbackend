package com.ssg.dsilbackend.dto.restaurantManage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuDTO {
    private Long id;
    private String name;
    private Long price;
    private String img;
    private String menuInfo;
    private Long restaurantId;
}
