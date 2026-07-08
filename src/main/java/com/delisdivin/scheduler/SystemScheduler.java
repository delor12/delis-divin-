package com.delisdivin.scheduler;

import com.delisdivin.entity.Subscription;
import com.delisdivin.repository.SubscriptionRepository;
import com.delisdivin.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SystemScheduler {

    private final BackupService backupService;
    private final SubscriptionRepository subscriptionRepository;

    /**
     * Run database backup at midnight every day
     * Cron expression: "0 0 0 * * ?"
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleDatabaseBackup() {
        log.info("Starting scheduled database backup...");
        try {
            backupService.backupDatabase();
            log.info("Scheduled database backup completed successfully.");
        } catch (Exception e) {
            log.error("Scheduled database backup failed: {}", e.getMessage());
        }
    }

    /**
     * Check subscriptions daily at 1:00 AM to see if they're expired
     * Cron expression: "0 0 1 * * ?"
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void scheduleSubscriptionCheck() {
        log.info("Starting scheduled subscription expiration check...");
        List<Subscription> activeSubscriptions = subscriptionRepository.findAll().stream()
                .filter(sub -> "ACTIVE".equals(sub.getStatus()))
                .toList();

        LocalDate today = LocalDate.now();
        int expiredCount = 0;

        for (Subscription sub : activeSubscriptions) {
            if (sub.getEndDate().isBefore(today)) {
                sub.setStatus("EXPIRED");
                subscriptionRepository.save(sub);
                expiredCount++;
                log.info("Subscription ID {} for Restaurant '{}' has expired.", sub.getId(), sub.getRestaurant().getName());
            }
        }
        log.info("Scheduled subscription check complete. Expired subscription count: {}", expiredCount);
    }
}
