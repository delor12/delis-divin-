package com.delisdivin.repository;

import com.delisdivin.entity.AppUser;
import com.delisdivin.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmail(String email);
    List<AppUser> findByRestaurantId(Long restaurantId);
    List<AppUser> findByRestaurantIdAndRole(Long restaurantId, Role role);
    List<AppUser> findByRole(Role role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
