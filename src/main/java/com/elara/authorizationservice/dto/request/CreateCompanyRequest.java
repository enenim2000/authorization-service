package com.elara.authorizationservice.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateCompanyRequest {

    private String companyName;

    private String companyAddress;

}
