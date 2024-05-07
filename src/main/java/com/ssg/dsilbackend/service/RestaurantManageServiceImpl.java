package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.domain.*;
import com.ssg.dsilbackend.dto.AvailableTimeTable;
import com.ssg.dsilbackend.dto.Crowd;
import com.ssg.dsilbackend.dto.reserve.ReserveDTO;
import com.ssg.dsilbackend.dto.restaurantManage.AvailableTimeDTO;
import com.ssg.dsilbackend.dto.restaurantManage.ReplyDTO;
import com.ssg.dsilbackend.dto.restaurantManage.RestaurantManageDTO;
import com.ssg.dsilbackend.dto.restaurantManage.ReviewDTO;
import com.ssg.dsilbackend.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
    @Autowired
    public RestaurantManageServiceImpl(RestaurantManageRepository restaurantManageRepository, ReserveRepository reserveRepository, ReviewRepository reviewRepository, ReplyRepository replyRepository, ModelMapper modelMapper) {
        this.restaurantManageRepository = restaurantManageRepository;
        this.reserveRepository = reserveRepository;
        this.reviewRepository = reviewRepository;
        this.replyRepository = replyRepository;
        this.modelMapper = modelMapper;
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
    public RestaurantManageDTO updateRestaurant(Long id, RestaurantManageDTO updatedRestaurantDTO) {
        Restaurant restaurant = restaurantManageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("식당 정보를 찾을 수 없습니다"));

        // 엔티티의 필드를 DTO에서 가져온 값으로 업데이트
        restaurant.setTel(updatedRestaurantDTO.getTel());
        restaurant.setImg(updatedRestaurantDTO.getImg());
        restaurant.setDeposit(updatedRestaurantDTO.getDeposit());
        restaurant.setTableCount(updatedRestaurantDTO.getTableCount());

        // 엔티티를 저장하고 업데이트된 DTO로 변환하여 반환
        return modelMapper.map(restaurantManageRepository.save(restaurant), RestaurantManageDTO.class);
    }

//    식당의 crowd를 변환하는 메소드
    @Override
    public RestaurantManageDTO updateCrowd(Long id, Crowd crowd) throws Exception {
        Restaurant restaurant = restaurantManageRepository.findById(id)
                .orElseThrow(() -> new Exception("Restaurant not found with id: " + id));

        restaurant.setCrowd(crowd);
        return modelMapper.map(restaurantManageRepository.save(restaurant), RestaurantManageDTO.class);
    }


    @Override
    public List<ReserveDTO> getReservationList(Long restaurantId) {
        List<Reservation> reservations = reserveRepository.findByRestaurantId(restaurantId);
        return reservations.stream()
                .map(reservation -> modelMapper.map(reservation, ReserveDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDTO getReview(Reservation reservation) {
        Review review = reviewRepository.findByReservation(reservation);
        return modelMapper.map(reviewRepository.save(review), ReviewDTO.class);
    }

    @Override
    public ReplyDTO createReply(Long reviewId, String content) {
        Reply newReply = Reply.builder()
                .content(content)
                .registerDate(LocalDate.now())
                .deleteStatus(false)
                .build();

        // 여기서 필요에 따라 reviewId에 해당하는 리뷰를 찾아서 newReply에 설정

        Reply savedReply = replyRepository.save(newReply);
        return modelMapper.map(savedReply, ReplyDTO.class);
    }

    @Override
    public AvailableTimeDTO createAvailableTime(Long restaurantId, AvailableTimeTable slot) {
        Optional<Restaurant> restaurant = restaurantManageRepository.findById(restaurantId);
        if (!restaurant.isPresent()) {
            throw new IllegalStateException("Restaurant with ID " + restaurantId + " not found");
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


}