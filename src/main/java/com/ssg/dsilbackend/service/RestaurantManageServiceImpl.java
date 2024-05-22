package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.domain.*;
import com.ssg.dsilbackend.dto.AvailableTimeTable;
import com.ssg.dsilbackend.dto.Crowd;
import com.ssg.dsilbackend.dto.ReservationStateName;
import com.ssg.dsilbackend.dto.restaurantManage.*;
import com.ssg.dsilbackend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.temporal.WeekFields;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.timeseries.core.TSLagUser;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.evaluation.NumericPrediction;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestaurantManageServiceImpl implements RestaurantManageService {

    @Autowired
    private AvailableTimeRepository availableTimeRepository;
    private final RestaurantManageRepository restaurantManageRepository;
    private final ReserveRepository reserveRepository;
    private final ReviewRepository reviewRepository;
    private final ReplyRepository replyRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final FacilityRepository facilityRepository;
    private final MenuRepository menuRepository;


    @Autowired
    public RestaurantManageServiceImpl(RestaurantManageRepository restaurantManageRepository, ReserveRepository reserveRepository, ReviewRepository reviewRepository, ReplyRepository replyRepository, ModelMapper modelMapper, RestaurantRepository restaurantRepository, CategoryRepository categoryRepository, FacilityRepository facilityRepository, MenuRepository menuRepository) {
        this.restaurantManageRepository = restaurantManageRepository;
        this.reserveRepository = reserveRepository;
        this.reviewRepository = reviewRepository;
        this.replyRepository = replyRepository;
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
        this.facilityRepository = facilityRepository;
        this.menuRepository = menuRepository;
    }

    @Override
    public RestaurantManageDTO getRestaurant(Long id) {
        Restaurant restaurant = restaurantManageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("식당 정보를 찾을 수 없습니다"));
        return modelMapper.map(restaurant, RestaurantManageDTO.class);
    }

    @Override
    public List<RestaurantManageDTO> getRestaurantList(Long memberId) {
        List<Restaurant> restaurants = restaurantManageRepository.findByMemberId(memberId);
        return restaurants.stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantManageDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantManageDTO updateRestaurant(Long id, RestaurantManageDTO restaurantDTO) {
        Restaurant restaurant = restaurantManageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        restaurant.updateRestaurant(
                restaurantDTO.getTel(),
                restaurantDTO.getImg(),
                restaurantDTO.getDeposit(),
                restaurantDTO.getTableCount(),
                restaurantDTO.getDescription()
        );

        updateCategories(restaurant, restaurantDTO.getCategories());
        updateFacilities(restaurant, restaurantDTO.getFacilities());
        updateMenus(restaurant, restaurantDTO.getMenus());

        return toManageDto(restaurant);
    }

    private void updateCategories(Restaurant restaurant, List<CategoryDTO> categoryDtos) {
        List<Category> existingCategories = categoryRepository.findByRestaurantId(restaurant.getId());

        // Delete categories that are not in the new list
        existingCategories.stream()
                .filter(existingCategory -> categoryDtos.stream()
                        .noneMatch(dto -> dto.getId() != null && dto.getId().equals(existingCategory.getId())))
                .forEach(categoryRepository::delete);

        // Add or update categories
        categoryDtos.forEach(dto -> {
            if (dto.getId() == null) {
                // Add new category
                Category category = Category.builder().name(dto.getName()).restaurant(restaurant).build();
                categoryRepository.save(category);
            } else {
                // Update existing category
                existingCategories.stream()
                        .filter(existingCategory -> existingCategory.getId().equals(dto.getId()))
                        .findFirst()
                        .ifPresent(existingCategory -> {
                            existingCategory.setCategoryName(dto.getName());
                            categoryRepository.save(existingCategory);
                        });
            }
        });
    }

    private void updateFacilities(Restaurant restaurant, List<FacilityDTO> facilityDtos) {
        List<Facility> existingFacilities = facilityRepository.findByRestaurantId(restaurant.getId());

        // Delete facilities that are not in the new list
        existingFacilities.stream()
                .filter(existingFacility -> facilityDtos.stream()
                        .noneMatch(dto -> dto.getId() != null && dto.getId().equals(existingFacility.getId())))
                .forEach(facilityRepository::delete);

        // Add or update facilities
        facilityDtos.forEach(dto -> {
            if (dto.getId() == null) {
                // Add new facility
                Facility facility = Facility.builder().name(dto.getName()).restaurant(restaurant).build();
                facilityRepository.save(facility);
            } else {
                // Update existing facility
                existingFacilities.stream()
                        .filter(existingFacility -> existingFacility.getId().equals(dto.getId()))
                        .findFirst()
                        .ifPresent(existingFacility -> {
                            existingFacility.setFacilityName(dto.getName());
                            facilityRepository.save(existingFacility);
                        });
            }
        });
    }

    private void updateMenus(Restaurant restaurant, List<MenuDTO> menuDtos) {
        List<Menu> existingMenus = menuRepository.findByRestaurantId(restaurant.getId());

        // Delete menus that are not in the new list
        existingMenus.stream()
                .filter(existingMenu -> menuDtos.stream()
                        .noneMatch(dto -> dto.getId() != null && dto.getId().equals(existingMenu.getId())))
                .forEach(menuRepository::delete);

        // Add or update menus
        menuDtos.forEach(dto -> {
            if (dto.getId() == null) {
                // Add new menu
                Menu menu = Menu.builder().name(dto.getName()).price(dto.getPrice()).img(dto.getImg()).menuInfo(dto.getMenuInfo()).restaurant(restaurant).build();
                menuRepository.save(menu);
            } else {
                // Update existing menu
                existingMenus.stream()
                        .filter(existingMenu -> existingMenu.getId().equals(dto.getId()))
                        .findFirst()
                        .ifPresent(existingMenu-> {
                            existingMenu.updateMenu(dto.getId(), dto.getName(), dto.getPrice(), dto.getImg(), dto.getMenuInfo());
                            menuRepository.save(existingMenu);
                        });
            }
        });
    }

    private RestaurantManageDTO toManageDto(Restaurant restaurant) {
        return RestaurantManageDTO.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .tel(restaurant.getTel())
                .crowd(restaurant.getCrowd())
                .img(restaurant.getImg())
                .deposit(restaurant.getDeposit())
                .tableCount(restaurant.getTableCount())
                .description(restaurant.getDescription())
                .memberId(restaurant.getMember().getId())
                .categories(categoryRepository.findByRestaurantId(restaurant.getId()).stream()
                        .map(this::toCategoryDto)
                        .collect(Collectors.toList()))
                .facilities(facilityRepository.findByRestaurantId(restaurant.getId()).stream()
                        .map(this::toFacilityDto)
                        .collect(Collectors.toList()))
                .menus(menuRepository.findByRestaurantId(restaurant.getId()).stream()
                        .map(this::toMenuDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private CategoryDTO toCategoryDto(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .restaurantId(category.getRestaurant().getId())
                .build();
    }

    private FacilityDTO toFacilityDto(Facility facility) {
        return FacilityDTO.builder()
                .id(facility.getId())
                .name(facility.getName())
                .restaurantId(facility.getRestaurant().getId())
                .build();
    }

    private MenuDTO toMenuDto(Menu menu) {
        return MenuDTO.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .img(menu.getImg())
                .menuInfo(menu.getMenuInfo())
                .restaurantId(menu.getRestaurant().getId())
                .build();
    }


//    @Override
//    @Transactional
//    public RestaurantManageDTO updateRestaurant(Long id, RestaurantManageDTO restaurantDTO) {
//        Restaurant restaurant = restaurantManageRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Restaurant with ID " + id + " not found"));
//
//        // 업데이트 가능한 필드를 설정
//        restaurant.updateRestaurant(restaurantDTO.getTel(), restaurantDTO.getImg(), restaurantDTO.getDeposit(), restaurantDTO.getTableCount(), restaurant.getDescription());
//
//        // 식당 정보 업데이트 후 저장
//        Restaurant updatedRestaurant = restaurantManageRepository.save(restaurant);
//
//        // 업데이트된 정보를 DTO로 변환하여 반환
//        return convertToRestaurantDTO(updatedRestaurant);
//    }
//
//    private RestaurantManageDTO convertToRestaurantDTO(Restaurant restaurant) {
//        return RestaurantManageDTO.builder()
//                .id(restaurant.getId())
//                .name(restaurant.getName())
//                .address(restaurant.getAddress())
//                .tel(restaurant.getTel())
//                .crowd(restaurant.getCrowd())
//                .img(restaurant.getImg())
//                .deposit(restaurant.getDeposit())
//                .tableCount(restaurant.getTableCount())
//                .description(restaurant.getDescription())
//                .memberId(restaurant.getMember().getId())
//                .build();
//    }







    //    식당의 crowd를 변환하는 메소드
    @Override
    public RestaurantManageDTO updateCrowd(Long id, Crowd crowd) throws Exception {
        Restaurant restaurant = restaurantManageRepository.findById(id)
                .orElseThrow(() -> new Exception("Restaurant not found with id: " + id));

        restaurant.setRestaurantCrowd(crowd);
        Restaurant savedRestaurant = restaurantManageRepository.save(restaurant);

        return modelMapper.map(savedRestaurant, RestaurantManageDTO.class);
    }


    @Override
    public List<ReservationDTO> getReservationList(Long restaurantId) {
        List<Reservation> reservations = reserveRepository.findByRestaurantIdAndReservationStateName(restaurantId, ReservationStateName.RESERVED);
        return reservations.stream()
                .map(this::convertToReserveDto)
                .collect(Collectors.toList());
    }
    private ReservationDTO convertToReserveDto(Reservation reservation) {
        return ReservationDTO.builder()
                .id(reservation.getId())
                .restaurantId(reservation.getRestaurant().getId())
                .memberId(reservation.getMembers().getId())
                .reservationStateName(ReservationStateName.valueOf(reservation.getReservationStateName().name()))
                .peopleCount(reservation.getPeopleCount())
                .reservationTime(reservation.getReservationTime())
                .reservationName(reservation.getReservationName())
                .requestContent(reservation.getRequestContent())
                .reservationDate(reservation.getReservationDate())
                .reservationTel(reservation.getReservationTel())
                .build();
    }

//    @Override
//    public ReviewDTO getReview(Reservation reservation) {
//        Review review = reviewRepository.findByReservation(reservation);
//        return modelMapper.map(reviewRepository.save(review), ReviewDTO.class);
//    }

    @Override
    public ReplyDTO createReply(Long reviewId, String content) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        Reply newReply = new Reply();
        newReply.setContent(content);
        newReply.setRegisterDate(LocalDate.now());
        newReply.setDeleteStatus(false);

        Reply savedReply = replyRepository.save(newReply);

        // 리뷰에 답글 설정
        review.setReply(savedReply);
        reviewRepository.save(review);

        return convertToReplyDto(savedReply);
    }
    private ReplyDTO convertToReplyDto(Reply reply) {
        return ReplyDTO.builder()
                .id(reply.getId())
                .content(reply.getContent())
                .registerDate(reply.getRegisterDate())
                .deleteStatus(reply.getDeleteStatus())
                .build();
    }


    @Override
    public AvailableTimeDTO createAvailableTime(Long restaurantId, AvailableTimeTable slot) {
        Optional<Restaurant> restaurant = restaurantManageRepository.findById(restaurantId);
        if (!restaurant.isPresent()) {
            throw new IllegalStateException("Restaurant with ID " + restaurantId + " not found");
        }

        // 동일한 시간대의 AvailableTime이 이미 존재하는지 확인
        Optional<AvailableTime> existingAvailableTime = availableTimeRepository.findByRestaurantIdAndAvailableTime(restaurantId, slot);
        if (existingAvailableTime.isPresent()) {
            throw new IllegalStateException("Available time already exists for this slot");
        }

        AvailableTime availableTime = new AvailableTime();
        availableTime.setAvailableTime(slot);
        availableTime.setRestaurant(restaurant.get());
        AvailableTime saved = availableTimeRepository.save(availableTime);
        return new AvailableTimeDTO(saved.getId(), saved.getAvailableTime().name());
    }


    @Override
    public void deleteAvailableTime(Long restaurantId, AvailableTimeTable slot) {
        availableTimeRepository.deleteByRestaurantIdAndAvailableTime(restaurantId, slot);
    }

    public List<AvailableTimeDTO> getAvailableTimes(Long restaurantId) {
        List<AvailableTime> availableTimes = availableTimeRepository.findByRestaurantId(restaurantId);
        return availableTimes.stream()
                .map(this::convertToAvailablTimeDTO)
                .collect(Collectors.toList());
    }

    public AvailableTimeDTO convertToAvailablTimeDTO(AvailableTime availableTime){
        return AvailableTimeDTO.builder()
                .id(availableTime.getId())
                .availableTime(availableTime.getAvailableTime().toString())
                .build();
    }


    @Transactional
    public List<ReviewDTO> getReviewList(Long restaurantId) {
        List<Review> reviews = reviewRepository.findByReservationRestaurantId(restaurantId);

        return reviews.stream()
                .map(this::convertToReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDTO updateReviewDeleteStatus(Long reviewId, boolean deleteStatus) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
        review.setRestaurantReviewDeleteStatus(deleteStatus);
        reviewRepository.save(review);
        return convertToReviewDto(review);
    }


    private ReviewDTO convertToReviewDto(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .replyId(review.getReply().getId())
                .replyContent(review.getReply().getContent())
                .reservationId(review.getReservation().getId())
                .content(review.getContent())
                .registerDate(review.getRegisterDate())  // 예제에서 LocalDate로 가정
                .score(review.getScore())
                .deleteStatus(review.isDeleteStatus())
                .img(review.getImg())
                .reservationName(review.getReservation().getReservationName())
                .build();
    }

    @Override
    public List<CategoryDTO> getCategoryList(Long restaurantId) {
        List<Category> categoryList = categoryRepository.findByRestaurantId(restaurantId);
        System.out.println("불러온 카테고리엔티티들:"+categoryList);
        return categoryList.stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FacilityDTO> getFacilityList(Long restaurantId) {
        List<Facility> facilityList = facilityRepository.findByRestaurantId(restaurantId);
        System.out.println("불러온 편의시설엔티티들:"+facilityList);
        return facilityList.stream()
                .map(this::toFacilityDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuDTO> getMenuList(Long restaurantId) {
        List<Menu> menuList = menuRepository.findByRestaurantId(restaurantId);
        System.out.println("불러온 메뉴엔티티들:"+menuList);
        return menuList.stream()
                .map(this::toMenuDto)
                .collect(Collectors.toList());
    }

    public MenuDTO getMenuById(Long id) {
        Optional<Menu> optionalMenu = menuRepository.findById(id);
        if (optionalMenu.isPresent()) {
            Menu menu = optionalMenu.get();
            return MenuDTO.builder()
                    .id(menu.getId())
                    .name(menu.getName())
                    .price(menu.getPrice())
                    .img(menu.getImg())
                    .menuInfo(menu.getMenuInfo())
                    .restaurantId(menu.getRestaurant().getId())
                    .build();
        } else {
            return null; // or throw an exception
        }
    }

    @Override
    public Map<Integer, Long> getMonthlyReservations(Long restaurantId, int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        List<Reservation> reservations = reserveRepository.findReservationsByDateRange(startDate, endDate);

        return reservations.stream()
                .filter(r -> r.getRestaurant().getId().equals(restaurantId))
                .collect(Collectors.groupingBy(r -> r.getReservationDate().getMonthValue(), Collectors.counting()));
    }

    @Override
    public Map<Integer, Long> getWeeklyReservations(Long restaurantId, int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        List<Reservation> reservations = reserveRepository.findReservationsByDateRange(startDate, endDate);

        return reservations.stream()
                .filter(r -> r.getRestaurant().getId().equals(restaurantId))
                .collect(Collectors.groupingBy(r -> r.getReservationDate().get(weekFields.weekOfWeekBasedYear()), Collectors.counting()));
    }

    public List<ReservationDTO> getReservationsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Reservation> reservations = reserveRepository.findReservationsByDateRange(startDate, endDate);
        return reservations.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private ReservationDTO convertToDTO(Reservation reservation) {
        return ReservationDTO.builder()
                .id(reservation.getId())
                .restaurantId(reservation.getRestaurant().getId())
                .memberId(reservation.getMembers().getId())
                .reservationStateName(reservation.getReservationStateName())
                .peopleCount(reservation.getPeopleCount())
                .reservationTime(reservation.getReservationTime())
                .reservationName(reservation.getReservationName())
                .requestContent(reservation.getRequestContent())
                .reservationDate(reservation.getReservationDate())
                .reservationTel(reservation.getReservationTel())
                .build();
    }

}
