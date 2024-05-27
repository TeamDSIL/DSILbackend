package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyManageRepository extends JpaRepository<Reply, Long> {

}
