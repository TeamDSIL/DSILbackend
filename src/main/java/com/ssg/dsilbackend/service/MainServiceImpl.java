package com.ssg.dsilbackend.service;



import com.ssg.dsilbackend.domain.Restaurant;
import com.ssg.dsilbackend.dto.CategoryName;
import com.ssg.dsilbackend.dto.main.MainDTO;
import com.ssg.dsilbackend.dto.main.MainMapDTO;
import com.ssg.dsilbackend.dto.main.ToptenDTO;
import com.ssg.dsilbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
//@RequiredArgsConstructor
//@Transactional
public class MainServiceImpl implements MainService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private BookmarkRepository bookmarkRepository;


    @Override
    @Transactional(readOnly = true)
    public List<MainDTO> findByCategoryName(CategoryName categoryName) {
        return categoryRepository.findByName(categoryName).stream()
                .map(category -> new MainDTO(category.getRestaurant().getName(), category.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<MainMapDTO> getNearbyRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(restaurant-> new MainMapDTO(restaurant.getId(), restaurant.getName(),restaurant.getAddress()))
                .collect(Collectors.toList());

    }

    @Override
    public List<ToptenDTO> getTopViews(){
        Pageable topTen = PageRequest.of(0, 10);
        return restaurantRepository.findAllByOrderByCountDesc(topTen).stream()
                .map(restaurant -> new ToptenDTO(restaurant.getId(), restaurant.getName(), restaurant.getImg(), restaurant.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ToptenDTO> getTopReservations() {
        Pageable topTen = PageRequest.of(0, 10);
        return reservationRepository.findTopByReservations(topTen).stream()
                .map(restaurant -> new ToptenDTO(restaurant.getId(), restaurant.getName(), restaurant.getImg(), restaurant.getDescription()))
                .collect(Collectors.toList());
    }
    @Override
    public List<ToptenDTO> getTopBookmarks() {
        Pageable topTen = PageRequest.of(0, 10);
        return bookmarkRepository.findTopByBookmarks(topTen).stream()
                .map(restaurant -> new ToptenDTO(restaurant.getId(), restaurant.getName(), restaurant.getImg(), restaurant.getDescription()))
                .collect(Collectors.toList());
    }
}