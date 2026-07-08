package com.delisdivin.repository;

import com.delisdivin.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByActiveTrue();
    boolean existsByNameIgnoreCase(String name);
}
