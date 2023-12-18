package com.elara.authorizationservice.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGroupPermissionRequest {

    private long groupId;

    private List<String> permissionId;

}
