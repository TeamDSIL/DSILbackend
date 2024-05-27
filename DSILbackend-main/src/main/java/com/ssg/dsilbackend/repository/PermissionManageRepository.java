package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Permission;
import com.ssg.dsilbackend.dto.PermissionRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionManageRepository extends JpaRepository<Permission, Integer> {
    Permission findByPermission(PermissionRole permissionRole);
}
