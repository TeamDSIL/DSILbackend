package com.ssg.dsilbackend.dto.restaurantManage;

import com.ssg.dsilbackend.dto.Crowd;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RestaurantManageDTO {
    private Long id;
    private String name;
    private String address;
    private String tel;
    private Long deposit;
    private Long tableCount;
    private Crowd crowd;
    private String img;
    private String description;
    private Long memberId;
    private List<CategoryDTO> categories;
    private List<FacilityDTO> facilities;
    private List<MenuDTO> menus;
}
