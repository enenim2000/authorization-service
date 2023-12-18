package com.elara.authorizationservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {

    private String companyCode;

    private String email;

    private String phone;

    private String password;

}
