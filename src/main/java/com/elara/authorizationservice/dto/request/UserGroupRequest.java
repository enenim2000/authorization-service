package com.elara.authorizationservice.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGroupRequest {

    private String userId;

    private List<Long> groupIds;

}
