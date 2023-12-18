package com.elara.authorizationservice.enums;

public enum ResponseCode {
    SUCCESSFUL("000"),
    FORBIDDEN("403"),
    EXPECTATION_FAILED("417"),
    UN_AUTHORIZED("401"),
    REFRESH_TOKEN_EXPIRED("401"),
    ACCESS_TOKEN_EXPIRED("419"),
    INTERNAL_SERVER_ERROR("500");

    String value;

    public String getValue(){
        return value;
    }

    ResponseCode(String value) {
        this.value = value;
    }

}
