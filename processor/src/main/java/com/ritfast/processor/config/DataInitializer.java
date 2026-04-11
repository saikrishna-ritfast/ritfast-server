package com.ritfast.processor.config;

import com.ritfast.common.enums.RoleCode;
import com.ritfast.processor.entity.Role;
import com.ritfast.processor.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        Arrays.stream(RoleCode.values()).forEach(roleCode -> {
            if (roleRepository.findByCode(roleCode).isEmpty()) {
                Role role = new Role();
                role.setCode(roleCode);
                role.setName("Role " + roleCode.name().replace("ROLE_", ""));
                roleRepository.save(role);
            }
        });
    }
}
