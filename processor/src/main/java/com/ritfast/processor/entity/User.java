package com.ritfast.processor.entity;

import java.util.HashSet;
import java.util.Set;

import com.ritfast.processor.dto.base.MetaData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.ritfast.common.enums.StatusCode;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends MetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(nullable = false, unique = true)
    private String emailId;

    @Column(unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean accountNonExpired = true;

    @Column(nullable = false)
    private Boolean accountNonLocked = true;

    @Column(nullable = false)
    private Boolean credentialsNonExpired = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCode statusCode = StatusCode.ACTIVE;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

}
