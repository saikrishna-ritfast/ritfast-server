package com.ritfast.processor.service;

import com.ritfast.common.enums.LevelType;
import com.ritfast.common.enums.RoleCode;
import com.ritfast.common.enums.StatusCode;
import com.ritfast.processor.dto.AuthResponse;
import com.ritfast.processor.dto.SignUpRequest;
import com.ritfast.processor.entity.Client;
import com.ritfast.processor.entity.Role;
import com.ritfast.processor.entity.User;
import com.ritfast.processor.repository.ClientRepository;
import com.ritfast.processor.repository.RoleRepository;
import com.ritfast.processor.repository.UserRepository;
import com.ritfast.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse signUp(SignUpRequest request) {
        if (userRepository.findByEmailId(request.getEmailId()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Create Client
        Client client = new Client();
        client.setName(request.getClientName());
        // Code generation logic or use default
        client.setCode(request.getClientName().toUpperCase().replaceAll("\\s+", "").substring(0, Math.min(request.getClientName().length(), 6)));
        client.setTypeCode("BUS");
        client.setLevelType(LevelType.CLIENT);
        client.setStatusCode(StatusCode.ACTIVE);
        client = clientRepository.save(client);

        // Fetch ROLE_OWNER
        Role ownerRole = roleRepository.findByCode(RoleCode.ROLE_OWNER)
                .orElseThrow(() -> new IllegalStateException("ROLE_OWNER not found in database"));

        // Create User
        User user = new User();
        user.setEmailId(request.getEmailId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setClient(client);
        user.getRoles().add(ownerRole);
        user = userRepository.save(user);

        // Extract authorities for response
        List<String> authorities = user.getRoles().stream()
                .map(r -> r.getCode().name())
                .collect(Collectors.toList());

        // We would add permissions to authorities too if they were fetched

        // Generate response
        AuthResponse.UserResponse userResponse = AuthResponse.UserResponse.builder()
                .id(user.getId())
                .clientId(client.getId())
                .emailId(user.getEmailId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .statusCode(user.getStatusCode().name())
                .stringAuthorities(authorities)
                .build();

        AuthResponse.ClientResponse clientResponse = AuthResponse.ClientResponse.builder()
                .id(client.getId())
                .code(client.getCode())
                .name(client.getName())
                .typeCode(client.getTypeCode())
                .levelType(client.getLevelType().name())
                .statusCode(client.getStatusCode().name())
                .build();

        String token = jwtTokenProvider.generateTokenFromEmail(user.getEmailId());

        return AuthResponse.builder()
                .user(userResponse)
                .client(clientResponse)
                .accessToken(token)
                .loggedInClientId(client.getId())
                .loggedInClientCode(client.getCode())
                .build();
    }
}

