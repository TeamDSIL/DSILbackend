package com.ssg.dsilbackend.dto.main;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ToptenDTO {
    private Long restaurantId;
    private String name;
    private String img;
}
