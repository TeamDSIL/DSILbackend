package com.ssg.dsilbackend.service;

import com.ssg.dsilbackend.domain.Menu;
import com.ssg.dsilbackend.domain.Restaurant;
import com.ssg.dsilbackend.dto.restaurantList.MenuListDTO;
import com.ssg.dsilbackend.dto.restaurantList.RestaurantListDTO;
import com.ssg.dsilbackend.repository.MenuRepository;
import com.ssg.dsilbackend.repository.ReserveRepository;
import com.ssg.dsilbackend.repository.RestaurantListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestaurantListServiceImpl implements RestaurantListService {
    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RestaurantListRepository restaurantListRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MenuListDTO> findMenus(Long id) {
        Optional<Restaurant> restaurantOpt = restaurantListRepository.findById(id);
        Restaurant restaurant = restaurantOpt.get();
        List<Menu> menus = menuRepository.findByRestaurantId(id);
        return menus.stream()
                .map(menu -> {
                    MenuListDTO dto = new MenuListDTO();
                    dto.setId(menu.getId());
                    dto.setName(menu.getName());
                    dto.setPrice(menu.getPrice());
//                    dto.setImg(menu.getImg());
                    // 레스토랑 정보 추가
                    dto.setRestaurant_id(restaurant.getId());
                    dto.setRestaurant_name(restaurant.getName());
                    dto.setRestaurant_address(restaurant.getAddress());
                    dto.setRestaurant_tel(restaurant.getTel());
                    dto.setRestaurant_crowd(restaurant.getCrowd());
//                    dto.setRestaurant_img(restaurant.getImg());
                    dto.setRestaurant_deposit(restaurant.getDeposit());
                    dto.setRestaurant_table_count(restaurant.getTableCount());
                    return dto;
                })
                .collect(Collectors.toList());
    }


}
