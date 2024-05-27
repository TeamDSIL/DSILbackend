package com.ssg.dsilbackend.dto.myDinig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    private String content;
    private LocalDate registerDate;
    private Long score;
    private String userEmail;
    private Long restaurantId;

}
