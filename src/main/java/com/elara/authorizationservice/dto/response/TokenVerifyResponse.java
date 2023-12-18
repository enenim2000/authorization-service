package com.elara.authorizationservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenVerifyResponse extends BaseResponse {

    private Data data;

    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Data {
        private String loginId;
        private String username;
        private String companyCode;
        private String companyName;
        private String phone;
        private String email;
        private String status;
        private boolean isEmailVerified;
        private boolean isPhoneVerified;
    }
}

