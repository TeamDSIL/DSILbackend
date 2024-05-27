package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Category;
import com.ssg.dsilbackend.dto.CategoryName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByRestaurantId(Long id);
    List<Category> findByName(CategoryName categoryName);
}



