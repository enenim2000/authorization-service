package com.elara.authorizationservice.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {

    private final NotificationCacheService cacheService;

    public SchedulerService(NotificationCacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Scheduled(cron = "${cron.cache.expiry}")
    public void deleteChatMessagesAutomatically() {
        cacheService.deleteExpiredOtp();
    }
}
