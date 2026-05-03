package com.ritfast.processor.dto.dealer;

import com.ritfast.common.enums.Tags;
import com.ritfast.processor.dto.base.MetaData;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class dealerResponse extends MetaData {
    private Long id;
    private Long clientId;
    private String name;
    private String companyName;
    private String phoneNumber;
    private String alternativeNumber;
    private Tags tag;
    private String location;
    private String statusCode;
}
