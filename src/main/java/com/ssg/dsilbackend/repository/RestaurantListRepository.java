package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Restaurant;
import com.ssg.dsilbackend.dto.CategoryName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantListRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findById(Long id);
    @Query("SELECT DISTINCT r FROM Restaurant r JOIN r.categories c WHERE c.name IN :categoryNames")
    List<Restaurant> findByCategoriesIn(@Param("categoryNames") List<CategoryName> categoryNames);
}
