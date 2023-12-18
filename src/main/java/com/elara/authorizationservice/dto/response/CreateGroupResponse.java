package com.elara.authorizationservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateGroupResponse extends BaseResponse {

    private Data data;

    public CreateGroupResponse() {
        super();
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private Long id;

        private String companyCode;

        private String groupName;

        private String description;

        private String status;

        private String createdAt;

        private String updatedAt;
    }
}
