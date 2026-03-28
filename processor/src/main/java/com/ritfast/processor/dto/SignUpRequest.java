package com.ritfast.processor.dto;

import lombok.Data;

@Data
public class SignUpRequest {
    private String emailId;
    private String password;
    private String firstName;
    private String lastName;
    private String clientName;
    private String phoneNumber;
}
