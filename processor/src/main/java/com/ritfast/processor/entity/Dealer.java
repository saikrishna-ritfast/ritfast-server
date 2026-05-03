package com.ritfast.processor.entity;

import com.ritfast.common.enums.StatusCode;
import com.ritfast.common.enums.Tags;
import com.ritfast.processor.dto.base.MetaData;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "dealers")
public class Dealer extends MetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    private String name;
    private String companyName;
    private String phoneNumber;
    private String alternativeNumber;
    private Tags tag;
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCode statusCode = StatusCode.ACTIVE;

}
