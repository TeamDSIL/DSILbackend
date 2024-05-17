package com.ssg.dsilbackend.dto.userManage;


import com.ssg.dsilbackend.domain.Permission;
import com.ssg.dsilbackend.domain.Point;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class UserManageDTO {
    private Long id;
    private String email;
    private String password;
    private Permission permission;
    private Point point;
    private String name;
    private String tel;
    private String address;
    private String postcode;
    private String status;

}

