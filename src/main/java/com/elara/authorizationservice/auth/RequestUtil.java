package com.elara.authorizationservice.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class RequestUtil {

    private static HttpServletRequest getRequest() {

        return  ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
    }

    public static AuthToken getAuthToken() {
        AuthToken authToken = (AuthToken) getRequest().getAttribute("auth_token");
        return authToken == null ? new AuthToken() : authToken;
    }

    public static void setAuthToken(AuthToken authToken) {
        getRequest().setAttribute("auth_token", authToken);
    }

    public static String getToken() {
        return getRequest().getHeader("Authorization").replace("Bearer ", "");
    }

    public static String getClientId() {
        return getRequest().getHeader("client-id");
    }

    public static String getClientIp() {
        String remoteIp;
        remoteIp = getRequest().getHeader("X-FORWARDED-FOR");
        if (remoteIp == null || "".equals(remoteIp)) {
            remoteIp = getRequest().getRemoteAddr();
        }
        return remoteIp;
    }

}
