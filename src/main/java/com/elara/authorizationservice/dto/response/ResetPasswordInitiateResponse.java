package com.elara.authorizationservice.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class ResetPasswordInitiateResponse extends BaseResponse {

    public ResetPasswordInitiateResponse() {
        super();
    }

    private Data data;

    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private String otpHash;
    }
}
