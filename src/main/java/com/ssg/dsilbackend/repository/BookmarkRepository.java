package com.ssg.dsilbackend.repository;

import com.ssg.dsilbackend.domain.Bookmark;
import com.ssg.dsilbackend.domain.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByMembersId(Long memberId);

    //다혜
    // 즐겨찾기 수가 많은 상위 10개 식당 조회
    @Query("SELECT b.restaurant FROM Bookmark b " +
            "GROUP BY b.restaurant.id ORDER BY COUNT(b.id) DESC")
    Page<Restaurant> findTopByBookmarks(Pageable pageable);

    
    //상현
    // 식당 상세페이지에서 즐겨찾기 추가 및 삭제 기능
    void deleteByMembersIdAndRestaurantId(Long memberId, Long restaurantId);
    boolean existsByMembersIdAndRestaurantId(Long memberId, Long restaurantId);
}
