package com.ritfast.processor.dto.User;

import com.ritfast.processor.dto.base.MetaData;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse extends MetaData {
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
