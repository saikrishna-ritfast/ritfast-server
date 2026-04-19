package com.ritfast.processor.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PasswordRequest extends LoginRequest {
    private String newPassword;
}
