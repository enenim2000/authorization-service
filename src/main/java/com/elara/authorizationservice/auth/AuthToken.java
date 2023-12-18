package com.elara.authorizationservice.auth;

import java.util.List;

import lombok.Data;

@Data
public class AuthToken {

  private String companyCode;

  private String companyName;

  private String uuid;

  private String email;

  private String phone;

  private String username;

  private String lang;

  private String accessToken;

  private String refreshToken;

  private boolean isEmailVerified;

  private boolean isPhoneVerified;

  List<String> audience;

  private String expires;
}
