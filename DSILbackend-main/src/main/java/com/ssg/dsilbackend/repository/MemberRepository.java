package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Members;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Members, Long> {
}
