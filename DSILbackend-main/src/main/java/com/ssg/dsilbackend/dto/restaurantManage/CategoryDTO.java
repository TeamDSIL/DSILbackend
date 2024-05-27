package com.ssg.dsilbackend.dto.restaurantManage;

import com.ssg.dsilbackend.dto.CategoryName;
import lombok.*;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    private Long id;
    private CategoryName name;
    private Long restaurantId;

    public CategoryDTO(Long id, CategoryName name) {
        if (id == null) {
            throw new IllegalArgumentException("Category id cannot be null");
        }
        this.id = id;
        this.name = name;
    }

    public void setId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Category id cannot be null");
        }
        this.id = id;
    }
}