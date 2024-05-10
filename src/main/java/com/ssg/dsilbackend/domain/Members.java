package com.ssg.dsilbackend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
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
    private boolean status = true;

    @Column(name = "member_address", length = 100, nullable = false)
    private String address;

    @Column(name = "member_postcode", nullable = false)
    private String postcode;

    @Column(name = "member_register_number", length = 20)
    private String registerNumber;

    public void setMemberState(Boolean status) {
        this.status = status;
    }

}
