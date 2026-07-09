package com.delisdivin.repository;

import com.delisdivin.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRestaurantIdAndClientUniqueIdOrderByTimestampAsc(Long restaurantId, String clientUniqueId);

    @Query("SELECT m FROM ChatMessage m WHERE m.restaurantId = :restaurantId AND m.id IN (SELECT MAX(m2.id) FROM ChatMessage m2 WHERE m2.restaurantId = :restaurantId GROUP BY m2.clientUniqueId) ORDER BY m.timestamp DESC")
    List<ChatMessage> findLatestMessagesGroupedByClient(Long restaurantId);
}
