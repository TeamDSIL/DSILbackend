package com.ssg.dsilbackend.repository;


import com.ssg.dsilbackend.domain.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {
    Boolean existsByRefresh(String refresh);

    void deleteByExpirationBefore(LocalDateTime now);
    void deleteByRefresh(String refresh);
}
