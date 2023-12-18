package com.elara.authorizationservice.dto.response;

import com.elara.authorizationservice.domain.ApplicationPermission;
import java.util.List;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupPermissionResponse extends BaseResponse{

    private long groupId;

    private List<ApplicationPermission> permissions;

}
