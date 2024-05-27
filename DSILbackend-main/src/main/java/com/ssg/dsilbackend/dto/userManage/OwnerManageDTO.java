package com.ssg.dsilbackend.dto.userManage;

import com.ssg.dsilbackend.domain.Permission;
import com.ssg.dsilbackend.domain.Point;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OwnerManageDTO {
    private Long id;
    private String email;
    private String name;
    private String ownerName;
    private String password;
    private Permission permission;
    private String tel;
    private String address;
    private String registerNumber;
}
