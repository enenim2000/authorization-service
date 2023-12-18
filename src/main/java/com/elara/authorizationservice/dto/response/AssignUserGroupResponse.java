package com.elara.authorizationservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignUserGroupResponse extends BaseResponse {
    public AssignUserGroupResponse() {
        super();
    }
}
