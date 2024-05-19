package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Members;
import com.ssg.dsilbackend.dto.PermissionRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserManageRepository extends JpaRepository<Members, Long> {
    Boolean existsByEmail(String email);

    Optional<Members> findByEmail(String email);

    @Query("SELECT m FROM Members m JOIN m.permission p WHERE p.permission = :permissionRole AND m.status = :status")
    List<Members> findMembersByPermissionRoleAndStatus(@Param("permissionRole") PermissionRole permissionRole, @Param("status") Boolean status);

    @Query("SELECT m FROM Members m JOIN m.permission p WHERE p.permission = :permissionRole AND m.status = :status AND m.email = :email")
    Members findMembersByPermissionRoleAndStatusAndEmail(@Param("permissionRole") PermissionRole permissionRole, @Param("status") Boolean status, String email);

}


