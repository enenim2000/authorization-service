package com.elara.authorizationservice.dto.response;

import com.elara.authorizationservice.auth.AuthToken;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponse extends BaseResponse {

    private AuthToken data;
}
