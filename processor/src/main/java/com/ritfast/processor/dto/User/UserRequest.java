package com.ritfast.processor.dto.User;

import com.ritfast.common.enums.RoleCode;
import lombok.Data;

@Data
public class UserRequest {
    private String emailId;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private RoleCode roleCode;
}
