package com.ssg.dsilbackend.repository;



import com.ssg.dsilbackend.domain.Restaurant;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Page<Restaurant> findAllByOrderByCountDesc(Pageable pageable);

    List<Restaurant> findByMemberId(Long memberId);
    Restaurant getRestaurantById(Long id);
    Restaurant findRestaurantByName(String restaurantName);
    void removeByNameAndAddress(String restaurantName, String address);
}