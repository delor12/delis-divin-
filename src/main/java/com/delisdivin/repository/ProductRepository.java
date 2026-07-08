package com.delisdivin.repository;

import com.delisdivin.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByRestaurantId(Long restaurantId);
    List<Product> findByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId);
    List<Product> findByRestaurantIdAndAvailableTrue(Long restaurantId);
    List<Product> findByRestaurantIdAndBeverageTrueAndAvailableTrue(Long restaurantId);
    List<Product> findByRestaurantIdAndDessertTrueAndAvailableTrue(Long restaurantId);
    List<Product> findByRestaurantIdAndStockQuantityLessThan(Long restaurantId, Integer threshold);
}
