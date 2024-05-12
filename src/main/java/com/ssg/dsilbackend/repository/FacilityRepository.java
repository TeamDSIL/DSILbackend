package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    List<Facility> findByRestaurantId(Long id);
}