package com.ssg.dsilbackend.domain;

import com.ssg.dsilbackend.dto.CategoryName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "restaurant_category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categories_id")
    private Long id;

    @Column(name = "categories_name", length = 100, nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryName name;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    public void setCategoryName(CategoryName name){
        this.name = name;
    }

}
