package com.elara.authorizationservice.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateCompanyRequest {

    private String companyName;

    private String companyCode;

    private String companyAddress;

}
