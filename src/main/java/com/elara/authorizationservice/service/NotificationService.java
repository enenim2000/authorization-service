package com.elara.authorizationservice.service;

import com.elara.authorizationservice.domain.User;
import com.elara.authorizationservice.dto.request.NotificationRequest;
import com.elara.authorizationservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class NotificationService {

  private final MailService mailService;
  private final SmsService smsService;
  private final NotificationCacheService cacheService;
  private final UserRepository userRepository;

  public NotificationService(MailService mailService,
                             SmsService smsService,
                             NotificationCacheService cacheService,
                             UserRepository userRepository) {
    this.mailService = mailService;
    this.smsService = smsService;
    this.cacheService = cacheService;
    this.userRepository = userRepository;
  }

  public void sendEmail(NotificationRequest dto) {
    new Thread(() -> {
      log.info("Sending... email to notification service");
      mailService.sendNotification(dto);
    }).start();
  }

  public void sendSms(NotificationRequest dto) {
    new Thread(() -> {
      log.info("Sending... sms to notification service");
      log.info("SMS message: {}", dto.getMessage());
      smsService.sendMessage(dto);
    }).start();
  }

  public void sendNotification(NotificationRequest dto, String otp) {
    if (dto != null) {
      User user = userRepository.findByCompanyCodeAndEmailOrPhone(dto.getCompanyCode(), dto.getRecipientEmail(), dto.getRecipientPhone());
      if (dto.isRequiredValidation() && user != null) {
        dto.setMessage(dto.getMessage().replace("{0}", otp));
        if (StringUtils.hasText(dto.getHtml())) {
          dto.setHtml(dto.getHtml().replace("{0}", otp));
        }
        cacheService.put(dto.getCompanyCode(), user.getId(), dto.getValidationType(), otp);
      }

      if (StringUtils.hasText(dto.getRecipientEmail())) {
        sendEmail(dto);
      }

      if (StringUtils.hasText(dto.getRecipientPhone())) {
        sendSms(dto);
      }
    }
  }
}
