package com.ssg.dsilbackend.dto.restaurantList;

import com.ssg.dsilbackend.dto.Crowd;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantListDTO {
    private Long restaurant_id;
    private String restaurant_name;
    private String restaurant_address;
    private String restaurant_tel;
    private Crowd restaurant_crowd;
    private String restaurant_img;
    private Long restaurant_deposit;
    private Long restaurant_table_count;
}

