package com.elara.authorizationservice.dto.response;

import com.elara.authorizationservice.domain.UserGroup;
import java.util.List;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupResponse extends BaseResponse {

    private String userId;

    private List<UserGroup> userGroups;

}
