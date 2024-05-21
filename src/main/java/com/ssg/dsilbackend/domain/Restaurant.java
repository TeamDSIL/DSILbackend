package com.ssg.dsilbackend.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ssg.dsilbackend.dto.Crowd;
import com.ssg.dsilbackend.dto.userManage.OwnerManageDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "restaurant")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long id;

    @Column(name = "restaurant_name", length = 50, nullable = false)
    private String name;

    @Column(name = "restaurant_address", length = 100, nullable = false)
    private String address;

    @Column(name = "restaurant_tel", length = 20, nullable = false)
    private String tel;

    @Column(name = "restaurant_crowd", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private Crowd crowd;


    @Column(name = "restaurant_img", length = 500)
    private String img;

    @Column(name = "restaurant_deposit")
    private Long deposit;

    @Column(name = "restaurant_table_count", nullable = false)
    private Long tableCount;

    @Column(name = "restaurant_description", length = 100)
    private String description;

    @Column(name = "view_count")
    private Long count;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Members member;
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> categories;

    // JsonManagedReference: Menu와 Facility간 역참조를 막아 무한재귀를 막음
    @OneToMany(mappedBy = "restaurant")
    @JsonManagedReference
    private List<Menu> menus;

    @OneToMany(mappedBy = "restaurant")
    @JsonManagedReference
    private List<Facility> facilities;


    public void updateRestaurantInfo(OwnerManageDTO ownerManageDTO) {
        this.tel = ownerManageDTO.getTel();
        this.address = ownerManageDTO.getAddress();
    }


    public void setRestaurantCrowd(Crowd crowd) {
        this.crowd = crowd;
    }

    public void updateRestaurant(String tel, String img, Long deposit, Long tableCount, String description){
        this.tel = tel;
        this.img = img;
        this.deposit = deposit;
        this.tableCount = tableCount;
        this.description = description;
    }


}

