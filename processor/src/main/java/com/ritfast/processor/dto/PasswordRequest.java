package com.ritfast.processor.dto;

import lombok.Data;

@Data
public class PasswordRequest extends LoginRequest {
    private String newPassword;
}
