package com.ssg.dsilbackend.domain;

import com.ssg.dsilbackend.dto.PermissionRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long id;

    @Column(name = "permission_name")
    @Enumerated(EnumType.STRING)
    private PermissionRole permission;
}
