package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.domain.*;
import com.ssg.dsilbackend.dto.CategoryName;
import com.ssg.dsilbackend.dto.FacilityName;
import com.ssg.dsilbackend.dto.restaurantList.RestaurantBookmarkDTO;
import com.ssg.dsilbackend.dto.restaurantList.RestaurantDetailDTO;
import com.ssg.dsilbackend.dto.restaurantList.RestaurantListDTO;
import com.ssg.dsilbackend.dto.restaurantList.RestaurantReviewDTO;
import com.ssg.dsilbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Member;
import java.util.*;
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
    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<RestaurantDetailDTO> findMenus(Long id) {  // 식당아이디 기준으로 식당 상세페이지에 데이터를 불러오는 메소드
        Optional<Restaurant> restaurantOpt = restaurantListRepository.findById(id);
        Restaurant restaurant = restaurantOpt.get();
        List<Menu> menus = menuRepository.findByRestaurantId(id);
        List<Facility> facilities = facilityRepository.findByRestaurantId(id);
        List<Category> categories = categoryRepository.findByRestaurantId(id);
        List<String> facilityNames = facilities.stream()
                .map(facility -> facility.getName().toString())
                .collect(Collectors.toList());
        List<String> categoryNames = categories.stream()
                .map(category -> category.getName().toString())
                .collect(Collectors.toList());
        return menus.stream()
                .map(menu -> {
                    RestaurantDetailDTO dto = new RestaurantDetailDTO();
                    dto.setId(menu.getId());
                    dto.setName(menu.getName());
                    dto.setPrice(menu.getPrice());
                    dto.setImg(menu.getImg());
                    dto.setMenu_info(menu.getMenuInfo());
                    // 레스토랑 정보 추가
                    dto.setRestaurant_id(restaurant.getId());
                    dto.setRestaurant_name(restaurant.getName());
                    dto.setRestaurant_description(restaurant.getDescription());
                    dto.setRestaurant_address(restaurant.getAddress());
                    dto.setRestaurant_tel(restaurant.getTel());
                    dto.setRestaurant_crowd(restaurant.getCrowd());
                    dto.setRestaurant_img(restaurant.getImg());
                    dto.setRestaurant_deposit(restaurant.getDeposit());
                    dto.setRestaurant_table_count(restaurant.getTableCount());
                    dto.setFacilies(facilityNames);
                    dto.setCategories(categoryNames);


                    return dto;
                })
                .collect(Collectors.toList());
    }

    //
//    public List<RestaurantListDTO> findByCategoryNames(List<CategoryName> categoryNames) { // 여러 카테고리 이름을 기준으로 식당 데이터를 리스트로 출력하는 메소드
//        List<Restaurant> restaurants;
//        if (categoryNames == null || categoryNames.isEmpty()) {
//            restaurants = restaurantListRepository.findAll();
//        } else {
//            restaurants = restaurantListRepository.findByCategoriesIn(categoryNames);
//        }
//
//        return restaurants.stream()
//                .map(restaurant -> {
//                    List<CategoryName> categories = restaurant.getCategories().stream()
//                            .map(Category::getName)
//                            .collect(Collectors.toList());
//
//                    return RestaurantListDTO.builder()
//                            .categories(categoryNames)
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
    public List<RestaurantListDTO> findByFilters(List<CategoryName> categoryNames, List<FacilityName> facilityNames, String search) {
        Map<Long, List<Review>> reviewsGroupedByRestaurant = getReviewsGroupedByRestaurant();
        Map<Long, Double> averageScores = calculateAverageScores(reviewsGroupedByRestaurant);

        return restaurantListRepository.findAllWithDetails().stream()
                .map(restaurant -> {
                    int matchCount = 0;

                    // 카테고리 필터
                    if (categoryNames != null && !categoryNames.isEmpty() &&
                            restaurant.getCategories().stream()
                                    .map(Category::getName)
                                    .anyMatch(categoryNames::contains)) {
                        matchCount++;
                    }

                    // 시설 필터
                    if (facilityNames != null && !facilityNames.isEmpty() &&
                            restaurant.getFacilities().stream()
                                    .map(Facility::getName)
                                    .anyMatch(facilityNames::contains)) {
                        matchCount++;
                    }

                    // 검색어 필터
                    if (search != null && !search.isEmpty() &&
                            (restaurant.getName().toLowerCase().contains(search.toLowerCase()) ||
                                    restaurant.getCategories().stream()
                                            .map(Category::getName)
                                            .map(Enum::name)
                                            .anyMatch(category -> category.contains(search)) ||
                                    restaurant.getFacilities().stream()
                                            .map(Facility::getName)
                                            .map(Enum::name)
                                            .anyMatch(facility -> facility.contains(search)))) {
                        matchCount++;
                    }

                    // DTO 객체 생성
                    List<CategoryName> categories = restaurant.getCategories().stream()
                            .map(Category::getName)
                            .collect(Collectors.toList());

                    List<FacilityName> facilities = restaurant.getFacilities().stream()
                            .map(Facility::getName)
                            .collect(Collectors.toList());

                    // 평균 점수 가져오기
                    double averageScore = averageScores.getOrDefault(restaurant.getId(), 0.0);

                    RestaurantListDTO dto = RestaurantListDTO.builder()
                            .categoryNames(categories)
                            .restaurant_id(restaurant.getId())
                            .restaurant_name(restaurant.getName())
                            .restaurant_img(restaurant.getImg())
                            .restaurant_tel(restaurant.getTel())
                            .restaurant_deposit(restaurant.getDeposit())
                            .restaurant_crowd(restaurant.getCrowd())
                            .restaurant_table_count(restaurant.getTableCount())
                            .restaurant_address(restaurant.getAddress())
                            .facilityNames(facilities)
                            .score(averageScore)
                            .build();
                    dto.setMatchCount(matchCount);

                    return dto;
                })
                .filter(dto -> {
                    if ((categoryNames == null || categoryNames.isEmpty()) &&
                            (facilityNames == null || facilityNames.isEmpty()) &&
                            (search == null || search.isEmpty())) {
                        return true;
                    }
                    return dto.getMatchCount() > 0;
                })
                .sorted(Comparator.comparingInt(RestaurantListDTO::getMatchCount).reversed())
                .collect(Collectors.toList());
    }

    public Map<Long, Double> calculateAverageScores(Map<Long, List<Review>> reviewsGroupedByRestaurant) {
        return reviewsGroupedByRestaurant.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    List<Review> reviews = entry.getValue();
                    double averageScore = reviews.stream()
                            .mapToDouble(review -> review.getScore() != null ? review.getScore() : 0.0)
                            .average()
                            .orElse(0.0);
                    String formattedAverageScore = String.format("%.1f", averageScore);
                    return Double.parseDouble(formattedAverageScore);
                }
        ));
    }

    public Map<Long, List<Review>> getReviewsGroupedByRestaurant() {
        List<Review> reviews = reviewRepository.findAllWithReservation();
        return reviews.stream().collect(Collectors.groupingBy(review -> review.getReservation().getRestaurant().getId()));
    }

    @Override
    public List<RestaurantReviewDTO> getRestaurntReviewsListById(Long restaurantId) {
        List<Review> reviewList = reviewRepository.findByReservationRestaurantIdWithFetchJoin(restaurantId);
        double averageScore = reviewList.stream()
                .mapToDouble(review -> review.getScore() != null ? review.getScore() : 0.0)
                .average()
                .orElse(0.0);
        String formattedAverageScore = String.format("%.1f", averageScore);
        double roundedAverageScore = Double.parseDouble(formattedAverageScore);
        Map<Integer, Long> scoreCounts = reviewList.stream()
                .collect(Collectors.groupingBy(review -> review.getScore() != null ? review.getScore().intValue() : 0, Collectors.counting()));

        List<Long> ratingsCount = Arrays.asList(
                scoreCounts.getOrDefault(1, 0L),
                scoreCounts.getOrDefault(2, 0L),
                scoreCounts.getOrDefault(3, 0L),
                scoreCounts.getOrDefault(4, 0L),
                scoreCounts.getOrDefault(5, 0L)
        );

        return reviewList.stream()
                .sorted((r1, r2) -> r2.getRegisterDate().compareTo(r1.getRegisterDate()))
                .map(review -> RestaurantReviewDTO.builder()
                        .restaurant_id(restaurantId)
                        .content(review.getContent())
                        .score(Double.valueOf(review.getScore()))
                        .registerDate(review.getRegisterDate())
                        .review_img(review.getImg())
                        .name(String.valueOf(review.getReservation()))
                        .averageScore(roundedAverageScore)
                        .reviewCount((long) reviewList.size())
                        .ratingsCount(ratingsCount)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        restaurantListRepository.incrementViewCount(id);
    }


    // 북마크 추가
    public void addBookmark(Long memberId, Long restaurantId) {
        Members member = memberRepository.findById(memberId).orElseThrow();
        Restaurant restaurant = restaurantListRepository.findById(restaurantId).orElseThrow();
        Bookmark bookmark = new Bookmark(member, restaurant);
        bookmarkRepository.save(bookmark);
    }
//  북마크 삭제
    public void removeBookmark(RestaurantBookmarkDTO restaurantBookmarkDTO) {
        bookmarkRepository.deleteByMembersIdAndRestaurantId(restaurantBookmarkDTO.getMemberId(), restaurantBookmarkDTO.getRestaurantId());
    }
}

