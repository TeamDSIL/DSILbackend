package com.ssg.dsilbackend.domain;

import com.ssg.dsilbackend.dto.userManage.UserManageDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "members")
public class Members {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "point_id")
    private Point point;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @Column(name = "member_email", nullable = false, length = 100)
    private String email;

    @Column(name = "member_password", length = 100, nullable = false)
    private String password;

    @Column(name = "member_name", length = 100, nullable = false)
    private String name;

    @Column(name = "member_tel", length = 100, nullable = false)
    private String tel;

    @Column(name = "member_status", nullable = false)
    private boolean status;

    @Column(name = "member_address", length = 100, nullable = false)
    private String address;

    @Column(name = "member_postcode", nullable = false)
    private String postcode;

    @Column(name = "member_register_number", length = 20)
    private String registerNumber;


    public void updateMemberStatus(Boolean status) {
        this.status = status;
    }


    public void updatePassword(String password){
        this.password = password;
    }

    public void updateMemberInfo(UserManageDTO userManageDTO){
        this.name = userManageDTO.getName();
        this.tel = userManageDTO.getTel();
        this.address = userManageDTO.getAddress();
        this.postcode = userManageDTO.getPostcode();

        // Point 엔티티의 currentPoint 값을 수정
        if (userManageDTO.getPoint() != null) {
            this.point.setCurrentPoint(userManageDTO.getPoint().getCurrentPoint());
            this.point.setAccumulatePoint(userManageDTO.getPoint().getAccumulatePoint());
        }
    }
    public void setOwnerPostcode(){
        this.postcode = "000000";
    }


}
