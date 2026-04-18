package com.ritfast.processor.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ritfast.common.enums.PermissionCode;
import com.ritfast.processor.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByCode(PermissionCode code);
}