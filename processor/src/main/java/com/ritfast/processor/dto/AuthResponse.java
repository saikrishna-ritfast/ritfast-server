package com.ritfast.processor.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import com.ritfast.processor.dto.base.MetaData;
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private UserResponse user;
    private ClientResponse client;
    private String accessToken;
    private Long loggedInClientId;
    private String loggedInClientCode;

    @Data
    @Builder
    public static class UserResponse extends MetaData {
        private Long id;
        private Long clientId;
        private String emailId;
        private String firstName;
        private String lastName;
        private String statusCode;
        private List<String> stringAuthorities;
        private List<String> permissions;
    }

    @Data
    @Builder
    public static class ClientResponse extends MetaData {
        private Long id;
        private String code;
        private String name;
        private String typeCode;
        private String levelType;
        private String statusCode;
    }
}
