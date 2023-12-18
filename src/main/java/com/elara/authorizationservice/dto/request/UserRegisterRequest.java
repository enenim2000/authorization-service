package com.elara.authorizationservice.dto.request;

import com.elara.authorizationservice.validator.Required;
import com.elara.authorizationservice.validator.ValidEmail;
import com.elara.authorizationservice.validator.ValidPassword;
import lombok.Data;

@Data
public class UserRegisterRequest {

    @Required(message = "password.required")
    @ValidPassword(message = "password.valid")
    private String password;

    @Required(message = "email.required")
    @ValidEmail(message = "email.valid")
    private String email;

    @Required(message = "phone.required")
    private String phone;

}
