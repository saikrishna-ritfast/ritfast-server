package com.ritfast.processor.dto.User;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserResponse {
    private Long id;
    private Long clientId;
    private String emailId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String statusCode;
    private List<String> roles;
    private List<String> permissions;
}
