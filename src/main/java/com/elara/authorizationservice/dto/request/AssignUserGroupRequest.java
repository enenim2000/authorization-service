package com.elara.authorizationservice.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignUserGroupRequest {

    private String companyCode;
    private long userId;
    private List<Long> groupIds;
}
