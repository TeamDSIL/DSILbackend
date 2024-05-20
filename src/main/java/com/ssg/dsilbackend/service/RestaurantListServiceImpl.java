package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.domain.*;
import com.ssg.dsilbackend.dto.CategoryName;
import com.ssg.dsilbackend.dto.restaurantList.RestaurantDetailDTO;
import com.ssg.dsilbackend.dto.restaurantList.RestaurantListDTO;
import com.ssg.dsilbackend.dto.restaurantList.RestaurantReviewDTO;
import com.ssg.dsilbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor //생성자에 의한 의존 주입
public class RestaurantListServiceImpl implements RestaurantListService {
    private final MenuRepository menuRepository;
    private final RestaurantListRepository restaurantListRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final FacilityRepository facilityRepository;

    @Override
    public List<RestaurantDetailDTO> findMenus(Long id) {  // 식당아이디 기준으로 식당 상세페이지에 데이터를 불러오는 메소드
        Optional<Restaurant> restaurantOpt = restaurantListRepository.findById(id);
        Restaurant restaurant = restaurantOpt.get();
        List<Menu> menus = menuRepository.findByRestaurantId(id);
        List<Facility> facilities = facilityRepository.findByRestaurantId(id);
        List<String> facilityNames = facilities.stream()
                .map(facility -> facility.getName().toString())
                .collect(Collectors.toList());

        return menus.stream()
                .map(menu -> {
                    RestaurantDetailDTO dto = new RestaurantDetailDTO();
                    dto.setId(menu.getId());
                    dto.setName(menu.getName());
                    dto.setPrice(menu.getPrice());
                    dto.setImg(menu.getImg());
                    // 레스토랑 정보 추가
                    dto.setRestaurant_id(restaurant.getId());
                    dto.setRestaurant_name(restaurant.getName());
                    dto.setRestaurant_address(restaurant.getAddress());
                    dto.setRestaurant_tel(restaurant.getTel());
                    dto.setRestaurant_crowd(restaurant.getCrowd());
                    dto.setRestaurant_img(restaurant.getImg());
                    dto.setRestaurant_deposit(restaurant.getDeposit());
                    dto.setRestaurant_table_count(restaurant.getTableCount());
                    dto.setFacilies(facilityNames);


                    return dto;
                })
                .collect(Collectors.toList());
    }



    //    public List<RestaurantListDTO> findByCategoryName(CategoryName categoryName) { // 카테고리 이름을 기준으로 식당 데이터를 리스트로 출력하는 메소드
//        List<Restaurant> restaurants;
//        if (categoryName == null) {
//            restaurants = restaurantListRepository.findAll();
//        } else {
//            restaurants = restaurantListRepository.findByCategoryName(categoryName);
//        }
//        return restaurants.stream()
//                .map(restaurant -> {
//                    List<CategoryName> categories = restaurant.getCategories().stream()
//                            .map(category -> (category.getName()))
//                            .collect(Collectors.toList());
//
//                    return RestaurantListDTO.builder()
//                            .categoryName(categoryName)
//                            .restaurant_id(restaurant.getId())
//                            .restaurant_name(restaurant.getName())
//                            .restaurant_img(restaurant.getImg())
//                            .restaurant_tel(restaurant.getTel())
//                            .restaurant_deposit(restaurant.getDeposit())
//                            .restaurant_crowd(restaurant.getCrowd())
//                            .restaurant_table_count(restaurant.getTableCount())
//                            .restaurant_address(restaurant.getAddress())
//                            .categories(categories)
//                            .build();
//                })
//                .collect(Collectors.toList());
//    }
    public List<RestaurantListDTO> findByCategoryNames(List<CategoryName> categoryNames) { // 여러 카테고리 이름을 기준으로 식당 데이터를 리스트로 출력하는 메소드
        List<Restaurant> restaurants;
        if (categoryNames == null || categoryNames.isEmpty()) {
            restaurants = restaurantListRepository.findAll();
        } else {
            restaurants = restaurantListRepository.findByCategoriesIn(categoryNames);
        }

        return restaurants.stream()
                .map(restaurant -> {
                    List<CategoryName> categories = restaurant.getCategories().stream()
                            .map(Category::getName)
                            .collect(Collectors.toList());

                    return RestaurantListDTO.builder()
                            .categories(categoryNames)
                            .restaurant_id(restaurant.getId())
                            .restaurant_name(restaurant.getName())
                            .restaurant_img(restaurant.getImg())
                            .restaurant_tel(restaurant.getTel())
                            .restaurant_deposit(restaurant.getDeposit())
                            .restaurant_crowd(restaurant.getCrowd())
                            .restaurant_table_count(restaurant.getTableCount())
                            .restaurant_address(restaurant.getAddress())
                            .categories(categories)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantReviewDTO> getRestaurntReviewsListById(Long restaurantId) { // 식당 아이디를 기준으로 리뷰 데이터 불러오기
        List<Review> reviewList = reviewRepository.findByReservationRestaurantId(restaurantId);
        return reviewList.stream()
                .map(review ->
                        RestaurantReviewDTO.builder()
                                .restaurant_id(restaurantId)
                                .content(review.getContent())
                                .score(Double.valueOf(review.getScore()))
                                .registerDate(review.getRegisterDate())
                                .build())
                .collect(Collectors.toList());
    }

}

