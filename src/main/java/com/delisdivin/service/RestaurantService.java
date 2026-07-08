package com.delisdivin.service;

import com.delisdivin.dto.RestaurantDTO;
import java.util.List;

public interface RestaurantService {
    RestaurantDTO createRestaurant(RestaurantDTO restaurantDTO);
    RestaurantDTO updateRestaurant(Long id, RestaurantDTO restaurantDTO);
    RestaurantDTO getRestaurantById(Long id);
    List<RestaurantDTO> getAllActiveRestaurants();
    List<RestaurantDTO> getAllRestaurants();
    List<RestaurantDTO> getRestaurantsByCity(Long cityId);
    List<RestaurantDTO> searchRestaurants(Long cityId, String name);
    void deleteRestaurant(Long id);
    void subscribe(Long restaurantId, String planName, Double price, Integer durationMonths);
}
