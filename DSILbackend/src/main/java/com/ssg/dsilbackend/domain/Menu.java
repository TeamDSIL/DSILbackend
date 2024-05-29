package com.ssg.dsilbackend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    @JsonBackReference
    private Restaurant restaurant;

    public void updateMenu(Long id, String name, Long price, String img, String menuInfo){
        this.id = id;
        this.name = name;
        this.price = price;
        this.img = img;
        this.menuInfo = menuInfo;
    }

}
