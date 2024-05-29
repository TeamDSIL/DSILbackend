package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Restaurant;
import com.ssg.dsilbackend.dto.CategoryName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantListRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findById(Long id);
    @Query("SELECT DISTINCT r FROM Restaurant r JOIN r.categories c WHERE c.name IN :categoryNames")
    List<Restaurant> findByCategoriesIn(@Param("categoryNames") List<CategoryName> categoryNames);

    // 식당 id를 통해 식당 상세정보기 페이지로 갈 경우 view_count 증가
    @Modifying
    @Query("UPDATE Restaurant r SET r.count = r.count + 1 WHERE r.id = :id")
    void incrementViewCount(@Param("id") Long id);

    
    @Query("SELECT r FROM Restaurant r " +
            "LEFT JOIN FETCH r.categories c " +
            "LEFT JOIN FETCH r.facilities f")
    List<Restaurant> findAllWithDetails();
}
