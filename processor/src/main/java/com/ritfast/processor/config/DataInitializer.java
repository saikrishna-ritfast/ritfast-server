package com.ritfast.processor.config;

import com.ritfast.common.enums.PermissionCode;
import com.ritfast.common.enums.RoleCode;
import com.ritfast.processor.entity.Permission;
import com.ritfast.processor.entity.Role;
import com.ritfast.processor.repository.PermissionRepository;
import com.ritfast.processor.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) {

        // Initialize permissions
        Set<Permission> allPermissions = new HashSet<>();
        Arrays.stream(PermissionCode.values()).forEach(code -> {
            Permission permission = permissionRepository.findByCode(code)
                    .orElseGet(() -> {
                        Permission newPerm = new Permission();
                        newPerm.setCode(code);
                        newPerm.setName( code.name().replace("_", " "));
                        return permissionRepository.save(newPerm);
                    });
            allPermissions.add(permission);
        });

        // 2. Initialize roles and assign permissions
        Arrays.stream(RoleCode.values()).forEach(roleCode -> {
            Role role = roleRepository.findByCode(roleCode)
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setCode(roleCode);
                        newRole.setName("Role " + roleCode.name().replace("ROLE_", " "));
                        return newRole;
                    });

            // Assign ALL permissions to ROLE_OWNER
            // You can adjust this later to assign specific permissions to specific roles
            // (e.g. ROLE_EMPLOYEE)
            if (roleCode.equals(RoleCode.ROLE_OWNER)) {
                role.setPermissions(allPermissions);
            }

            if (roleCode.equals(RoleCode.ROLE_EMPLOYEE)) {
                List<String> employeePermissionCodes = List.of("USER_READ", "USER_UPDATE",
                        "CLIENT_READ", "DEALER_READ", "DEALER_UPDATE",
                        "DEALER_DELETE");

                Set<Permission> employeePermissions = allPermissions.stream()
                        .filter(p -> employeePermissionCodes.contains(p.getCode().name()))
                        .collect(java.util.stream.Collectors.toSet());

                role.setPermissions(employeePermissions);
            }
            roleRepository.save(role);
        });
    }
}