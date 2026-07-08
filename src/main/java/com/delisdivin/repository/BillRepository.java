package com.delisdivin.repository;

import com.delisdivin.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByRestaurantId(Long restaurantId);
    Optional<Bill> findByOrderId(Long orderId);
    Optional<Bill> findByBillNumber(String billNumber);
}
