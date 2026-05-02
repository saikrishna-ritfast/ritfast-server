package com.ritfast.processor.dto.base;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class MetaData {
    private Long createBy;
    private String createAt;
    private Long updateBy;
    private String updateAt;
}
