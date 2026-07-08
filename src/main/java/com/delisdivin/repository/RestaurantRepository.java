package com.delisdivin.repository;

import com.delisdivin.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByActiveTrue();
    List<Restaurant> findByCityIdAndActiveTrue(Long cityId);
    List<Restaurant> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    List<Restaurant> findByCityIdAndNameContainingIgnoreCaseAndActiveTrue(Long cityId, String name);
}
