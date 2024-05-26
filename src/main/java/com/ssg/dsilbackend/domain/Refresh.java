package com.ssg.dsilbackend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Refresh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_id")
    private Long id;

    @Column(name = "refresh_email")
    private String email;

    @Column(name = "refresh_token")
    private String refresh;

    @Column(name = "refresh_expiration")
    private String expiration;
}
