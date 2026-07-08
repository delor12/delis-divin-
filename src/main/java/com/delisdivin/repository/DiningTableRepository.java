package com.delisdivin.repository;

import com.delisdivin.entity.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {
    List<DiningTable> findByRestaurantId(Long restaurantId);
    List<DiningTable> findByRestaurantIdOrderByNumberAsc(Long restaurantId);
}
