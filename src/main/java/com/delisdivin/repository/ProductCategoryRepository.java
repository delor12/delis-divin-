package com.delisdivin.repository;

import com.delisdivin.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findByRestaurantId(Long restaurantId);
    List<ProductCategory> findByRestaurantIdAndActiveTrueOrderByDisplayOrderAsc(Long restaurantId);
}
