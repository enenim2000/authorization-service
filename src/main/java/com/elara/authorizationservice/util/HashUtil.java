package com.elara.authorizationservice.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HashUtil {

  public static String getHash(String data) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hash);
    } catch (Exception e) {
      log.error("Hashing Error:", e);
      return null;
    }
  }
}
