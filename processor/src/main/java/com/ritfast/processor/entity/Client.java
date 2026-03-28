package com.ritfast.processor.entity;

import com.ritfast.common.enums.LevelType;
import com.ritfast.common.enums.StatusCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clients")
@Getter
@Setter
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, unique = true, length = 20)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String typeCode = "BUS";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LevelType levelType = LevelType.CLIENT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCode statusCode = StatusCode.ACTIVE;
}
