package com.elara.authorizationservice.dto.request;

import com.elara.authorizationservice.validator.Required;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class UpdateApplicationRequest {

    @Required(message = "appName.required")
    private String appName;

    @Required(message = "appServer.required")
    private String appServer;

    @Required(message = "appServerPort.required")
    private String appServerPort;
}
