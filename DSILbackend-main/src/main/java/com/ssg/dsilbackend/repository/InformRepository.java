package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Inform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InformRepository extends JpaRepository<Inform, Long> {

}
