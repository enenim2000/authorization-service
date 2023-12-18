package com.elara.authorizationservice.config;

import com.elara.authorizationservice.util.PasswordEncoder;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BeanConfig {

 /* @Value("${spring.mail.host}")
  String mailHost;

  @Value("${spring.mail.username}")
  String mailUsername;

  @Value("${spring.mail.password}")
  String mailPassword;

  @Value("${spring.mail.properties.mail.smtp.auth}")
  boolean smtpAuth;

  @Value("${spring.mail.port}")
  Integer mailPort;

  @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
  boolean startTls;*/

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public Gson gson() {
    return new Gson();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new PasswordEncoder();
  }

  /*@Bean
  public JavaMailSender javaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(mailHost);
    mailSender.setPort(mailPort);
    mailSender.setUsername(mailUsername);
    mailSender.setPassword(mailPassword);

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", smtpAuth + "");
    props.put("mail.smtp.starttls.enable", startTls + "");
    props.put("mail.debug", "true");

    return mailSender;
  }*/
}
