package com.elara.authorizationservice.dto.request;

import com.elara.authorizationservice.validator.Required;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    @Required(message = "companyCode.required")
    private String companyCode;

    @Required(message = "email.required")
    private String email;

    @Required(message = "phone.required")
    private String phone;

    @Required(message = "lang.required")
    private String lang;
}
