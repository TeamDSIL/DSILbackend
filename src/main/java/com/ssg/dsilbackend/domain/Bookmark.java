package com.ssg.dsilbackend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "bookmark")
public class Bookmark {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmarks_id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Members members;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    // Constructor - 즐겨찾기 생성 
    public Bookmark(Members members, Restaurant restaurant) {
        this.members = members;
        this.restaurant = restaurant;
    }

    // Default constructor - 즐겨찾기 삭제
    protected Bookmark() {}


}
