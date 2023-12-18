package com.elara.authorizationservice.util;

import com.elara.authorizationservice.auth.RequestUtil;
import com.elara.authorizationservice.enums.ResponseCode;
import com.elara.authorizationservice.exception.UnAuthorizedException;
import com.elara.authorizationservice.domain.Company;
import com.elara.authorizationservice.service.MessageService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@Component
public class JWTTokens {

    @Value("${oauth.jwt.secret}")
    private String jwtSecret;

    @Value("${oauth.jwt.duration.expiry}")
    private String jwtDurationExpiry;

    private final MessageService messageService;

    public JWTTokens(MessageService messageService) {
        this.messageService = messageService;
    }

    private String createJWT(Company company, String username,  int timeExpiryInHours) {

        long timeInMillis = 1000L * 60 * 60 * timeExpiryInHours;

        long nowMillis = System.currentTimeMillis();

        String id = company.getCompanyCode() + "|" + username + "|" + company.getClientId();

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        HashMap<String, String> claims = new HashMap<>();
        claims.put("ip", RequestUtil.getClientIp());
        claims.put("subject", username);
        claims.put("issuer", company.getCompanyCode());
        claims.put("issuedAt", now.toString());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(username)
                .setClaims(claims)
                .setIssuer(company.getCompanyCode())
                .signWith(key, signatureAlgorithm);

        //if it has been specified, let's add the expiration
        if (timeInMillis >= 0) {
            long expMillis = nowMillis + timeInMillis;
            Date expiry = new Date(expMillis);
            builder.setExpiration(expiry);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public String generateAccessToken(Company company, String username) {
        int timeExpiry = Integer.parseInt(jwtDurationExpiry); //in hour
        return createJWT(company, username, timeExpiry);
    }

    public String generateRefreshToken(Company company, String username) {
        //Refresh token expires in 24 hours after the access token expires
        int timeExpiry = Integer.parseInt(jwtDurationExpiry) * 24;
        return createJWT(company, username, timeExpiry);
    }

    public Claims parseJWT(String jwtToken) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            // Get Claims from valid token
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken).getBody();
            if (!claims.get("ip").equals(RequestUtil.getClientIp())) {
                throw new UnAuthorizedException(messageService.getMessage("Token.Fraud"));
            }
            return claims;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature:", e);
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token:", e);
        } catch (ExpiredJwtException e) {
            log.info("Token expired: {}", e.getMessage());
            throw new UnAuthorizedException(ResponseCode.ACCESS_TOKEN_EXPIRED.getValue(), messageService.getMessage("Token.Expired"));
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported:", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty:", e);
        } catch (Exception e) {
            log.error("JWT token error:", e);
        }
        throw new UnAuthorizedException(ResponseCode.UN_AUTHORIZED.getValue(), messageService.getMessage("Token.Invalid"));
    }

    public Claims parseRefreshJWT(String jwtToken) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            // Get Claims from valid token
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken).getBody();
            if (!claims.get("ip").equals(RequestUtil.getClientIp())) {
                throw new UnAuthorizedException(messageService.getMessage("Token.Fraud"));
            }
            return claims;
        } catch (ExpiredJwtException e) {
            log.info("Refresh token expired: {}", e.getMessage());
            throw new UnAuthorizedException(ResponseCode.REFRESH_TOKEN_EXPIRED.getValue(), messageService.getMessage("Refresh.Token.Expired"));
        } catch (Exception e) {
            log.error("JWT token error:", e);
        }
        throw new UnAuthorizedException(ResponseCode.UN_AUTHORIZED.getValue(), messageService.getMessage("Token.Invalid"));
    }
}
