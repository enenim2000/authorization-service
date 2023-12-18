package com.elara.authorizationservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateApplicationResponse extends BaseResponse {

    private Data data;

    public CreateApplicationResponse() {
        super();
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private long id;

        private String appName;

        private String appServer;

        private String appServerPort;

        private String status;

        private String createdAt;

        private String updatedAt;
    }
}
