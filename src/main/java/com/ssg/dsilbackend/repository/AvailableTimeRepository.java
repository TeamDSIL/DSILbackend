package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.AvailableTime;
import com.ssg.dsilbackend.dto.AvailableTimeTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AvailableTimeRepository extends JpaRepository<AvailableTime, Long> {
    
    //특정 식당과 시간에 해당하는 availableTime 인스턴스를 찾는 메소드
    Optional<AvailableTime> findByRestaurantIdAndAvailableTime(Long restaurantId, AvailableTimeTable slot);
    //특정 식당과 시간에 해당하는 availableTime 인스턴스를 삭제하는 메소드
    void deleteByRestaurantIdAndAvailableTime(Long restaurantId, AvailableTimeTable slot);

    List<AvailableTime> findByRestaurantId(Long restaurantId);
}