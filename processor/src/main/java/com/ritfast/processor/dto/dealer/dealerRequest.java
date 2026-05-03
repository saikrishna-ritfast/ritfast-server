package com.ritfast.processor.dto.dealer;

import lombok.Data;

@Data
public class dealerRequest {
    private String name;
    private String companyName;
    private String phoneNumber;
    private String alternativeNumber;
    private String location;
}
