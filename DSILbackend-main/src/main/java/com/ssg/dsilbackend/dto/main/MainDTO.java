package com.ssg.dsilbackend.dto.main;

import com.ssg.dsilbackend.dto.CategoryName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainDTO {
    private String restaurantName;
    private CategoryName categoryName;
}