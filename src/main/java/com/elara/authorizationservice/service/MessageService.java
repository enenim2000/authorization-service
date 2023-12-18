package com.elara.authorizationservice.service;

import com.elara.authorizationservice.auth.RequestUtil;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageService {

    private final MessageSource messageSource;

    @Autowired
    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, getLocale());
    }

    private Locale getLocale() {
        String lang = RequestUtil.getAuthToken().getLang();
        if (lang == null || lang.trim().equals("")) {
            log.info("Locale language: {}", Locale.getDefault().getLanguage());
            lang = Locale.getDefault().getLanguage();
        }

        return new Locale.Builder()
                .setLanguage(lang)
                .build();
    }

}
