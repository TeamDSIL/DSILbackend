package com.ssg.dsilbackend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "restaurant_menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @Column(name = "menu_name", length = 100, nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;



    @Column(name = "menu_img", length = 500)
    private String img;

    @Column(name = "menu_info", length = 200)
    private String menuInfo;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    public void updateMenu(String name, Long price, String img, String menuInfo){
        this.name = name;
        this.price = price;
        this.img = img;
        this.menuInfo = menuInfo;
    }

}
