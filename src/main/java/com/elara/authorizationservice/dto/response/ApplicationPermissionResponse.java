package com.elara.authorizationservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationPermissionResponse extends BaseResponse {

    private long id;

    private String permissionId;

    private String permission;

    private String description;

    private String uriPath;

    private boolean isSecured;

    private String status;

    private String createdAt;

    private String updatedAt;

}
