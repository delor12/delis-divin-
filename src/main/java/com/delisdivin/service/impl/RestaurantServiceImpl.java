package com.delisdivin.service.impl;

import com.delisdivin.dto.RestaurantDTO;
import com.delisdivin.entity.City;
import com.delisdivin.entity.Restaurant;
import com.delisdivin.entity.Subscription;
import com.delisdivin.exception.ResourceNotFoundException;
import com.delisdivin.mapper.AppMapper;
import com.delisdivin.repository.CityRepository;
import com.delisdivin.repository.RestaurantRepository;
import com.delisdivin.repository.SubscriptionRepository;
import com.delisdivin.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final CityRepository cityRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AppMapper mapper;

    @Override
    @Transactional
    public RestaurantDTO createRestaurant(RestaurantDTO dto) {
        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found with ID: " + dto.getCityId()));
        
        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.getName());
        restaurant.setDescription(dto.getDescription());
        restaurant.setAddress(dto.getAddress());
        restaurant.setCity(city);
        restaurant.setPhone(dto.getPhone());
        restaurant.setEmail(dto.getEmail());
        restaurant.setPriceRange(dto.getPriceRange());
        restaurant.setAveragePrepTime(dto.getAveragePrepTime());
        restaurant.setLogoUrl(dto.getLogoUrl());
        restaurant.setBannerUrl(dto.getBannerUrl());
        if (dto.getLatitude() != null) {
            restaurant.setLatitude(dto.getLatitude());
        }
        if (dto.getLongitude() != null) {
            restaurant.setLongitude(dto.getLongitude());
        }
        restaurant.setActive(dto.isActive());
        
        Restaurant saved = restaurantRepository.save(restaurant);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public RestaurantDTO updateRestaurant(Long id, RestaurantDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + id));
        
        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found with ID: " + dto.getCityId()));
        
        restaurant.setName(dto.getName());
        restaurant.setDescription(dto.getDescription());
        restaurant.setAddress(dto.getAddress());
        restaurant.setCity(city);
        restaurant.setPhone(dto.getPhone());
        restaurant.setEmail(dto.getEmail());
        restaurant.setPriceRange(dto.getPriceRange());
        restaurant.setAveragePrepTime(dto.getAveragePrepTime());
        restaurant.setLogoUrl(dto.getLogoUrl());
        restaurant.setBannerUrl(dto.getBannerUrl());
        if (dto.getLatitude() != null) {
            restaurant.setLatitude(dto.getLatitude());
        }
        if (dto.getLongitude() != null) {
            restaurant.setLongitude(dto.getLongitude());
        }
        restaurant.setActive(dto.isActive());
        
        Restaurant updated = restaurantRepository.save(restaurant);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantDTO getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + id));
        return mapper.toDto(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantDTO> getAllActiveRestaurants() {
        return restaurantRepository.findByActiveTrue().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantDTO> getRestaurantsByCity(Long cityId) {
        return restaurantRepository.findByCityIdAndActiveTrue(cityId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantDTO> searchRestaurants(Long cityId, String name) {
        if (cityId != null && name != null && !name.isBlank()) {
            return restaurantRepository.findByCityIdAndNameContainingIgnoreCaseAndActiveTrue(cityId, name).stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
        } else if (cityId != null) {
            return getRestaurantsByCity(cityId);
        } else if (name != null && !name.isBlank()) {
            return restaurantRepository.findByNameContainingIgnoreCaseAndActiveTrue(name).stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return getAllActiveRestaurants();
        }
    }

    @Override
    @Transactional
    public void deleteRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + id));
        restaurantRepository.delete(restaurant);
    }

    @Override
    @Transactional
    public void subscribe(Long restaurantId, String planName, Double price, Integer durationMonths) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));
        
        Subscription subscription = new Subscription();
        subscription.setRestaurant(restaurant);
        subscription.setPlanName(planName);
        subscription.setPrice(price);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(durationMonths));
        subscription.setStatus("ACTIVE");
        
        subscriptionRepository.save(subscription);
    }
}
