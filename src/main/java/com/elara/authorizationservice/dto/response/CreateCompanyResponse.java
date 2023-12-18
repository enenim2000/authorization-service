package com.elara.authorizationservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateCompanyResponse extends BaseResponse {

    private Data data;

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private Long id;

        private String companyName;

        private String companyCode;

        private String companyAddress;

        private String clientId;

        private String clientSecret;

        private String status;

        private String createdAt;

        private String updatedAt;
    }
}
