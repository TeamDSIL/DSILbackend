package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointManageRepository extends JpaRepository<Point, Long> {
}
