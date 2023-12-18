package com.elara.authorizationservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationAccountResponse extends BaseResponse {

    private long id;

    private String companyCode;

    private String userId;

    private String permissionId;

    private String status;

    private String createdAt;

    private String updatedAt;

}
