package com.elara.authorizationservice.service;

import com.elara.authorizationservice.domain.NotificationCache;
import com.elara.authorizationservice.enums.NotificationType;
import com.elara.authorizationservice.repository.NotificationCacheRepository;
import com.elara.authorizationservice.util.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class NotificationCacheService {

  private final NotificationCacheRepository cacheRepository;

  @Value("${transaction.otp.expiry}")
  private String transactionExpiry;

  @Value("${verify.otp.expiry}")
  private String verifyOtpExpiry;

  public NotificationCacheService(NotificationCacheRepository cacheRepository) {
    this.cacheRepository = cacheRepository;
  }

  public void put(String companyCode, long userId, NotificationType notificationType, String otp) {
    Date now = new Date();
    long MINUTES = 60 * 1000;
    int expiry;

    if (NotificationType.TransactionVerify.name().equalsIgnoreCase(notificationType.name())) {
      expiry = Integer.parseInt(transactionExpiry);
    } else {
      expiry = Integer.parseInt(verifyOtpExpiry);
    }

    Date newDate = new Date(now.getTime() + expiry * MINUTES);

    String token = HashUtil.getHash(companyCode + userId + notificationType.name() + otp);
    NotificationCache notificationCache = cacheRepository.findByToken(token);

    if (notificationCache == null) {
      notificationCache = new NotificationCache();
    }

    notificationCache.setNotificationType(notificationType.name());
    notificationCache.setOtp(otp);
    notificationCache.setToken(token);
    notificationCache.setExpiry(newDate);
    notificationCache.setUserId(userId);
    notificationCache.setCompanyCode(companyCode);
    cacheRepository.save(notificationCache);
  }

  private NotificationCache get(String companyCode, long userId, NotificationType notificationType, String otp) {
    String token = HashUtil.getHash(companyCode + userId + notificationType.name() + otp);
    return cacheRepository.findByToken(token);
  }

  public boolean isValid(String companyCode, long userId, NotificationType notificationType, String otp) {
    NotificationCache cache = get(companyCode, userId, notificationType, otp);
    if (cache == null) {
      return false;
    }
    return new Date().before(cache.getExpiry());
  }

  public void deleteExpiredOtp() {
    new Thread(() -> {
      cacheRepository.deleteExpiredOtp(new Date());
    }).start();
  }

  public void deleteUsedOtp(String companyCode, Long userId, NotificationType notificationType, String otp) {
    new Thread( () -> {
      String token = HashUtil.getHash(companyCode + userId + notificationType.name() + otp);
      cacheRepository.deleteUsedOtp(token);
    }).start();
  }
}
