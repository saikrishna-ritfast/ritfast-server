package com.ritfast.security.userdetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SecurityUserRepository extends JpaRepository<SecurityUserEntity, Long> {
    
    @Query("SELECT u FROM SecurityUserEntity u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.emailId = :email")
    Optional<SecurityUserEntity> findByEmailWithRoles(@Param("email") String email);
}
