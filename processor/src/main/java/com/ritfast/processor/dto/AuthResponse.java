package com.ritfast.processor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

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
    public static class UserResponse {
        private Long id;
        private Long clientId;
        private String emailId;
        private String firstName;
        private String lastName;
        private String statusCode;
        private java.util.List<String> stringAuthorities;
    }

    @Data
    @Builder
    public static class ClientResponse {
        private Long id;
        private String code;
        private String name;
        private String typeCode;
        private String levelType;
        private String statusCode;
    }
}
