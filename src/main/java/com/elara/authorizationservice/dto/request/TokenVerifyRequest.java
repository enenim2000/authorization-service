package com.elara.authorizationservice.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenVerifyRequest {

    //Client id of the service on application table
    private String serviceClientId;
    private String token;
    private String permissionId;
}
