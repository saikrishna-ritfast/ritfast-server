package com.ritfast.processor.service;

import com.ritfast.common.enums.LevelType;
import com.ritfast.common.enums.RoleCode;
import com.ritfast.common.enums.StatusCode;
import com.ritfast.processor.dto.AuthResponse;
import com.ritfast.processor.dto.LoginRequest;
import com.ritfast.processor.dto.PasswordRequest;
import com.ritfast.processor.dto.SignUpRequest;
import com.ritfast.processor.entity.Client;
import com.ritfast.processor.entity.Role;
import com.ritfast.processor.entity.User;
import com.ritfast.processor.repository.ClientRepository;
import com.ritfast.processor.repository.RoleRepository;
import com.ritfast.processor.repository.UserRepository;
import com.ritfast.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final ClientRepository clientRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtTokenProvider jwtTokenProvider;
        private final AuthenticationManager authenticationManager;

        @Transactional
        public AuthResponse signUp(SignUpRequest request) {

                if (userRepository.findByEmailId(request.getEmailId()).isPresent()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
                }

                // Create Client
                Client client = new Client();
                client.setName(request.getClientName());
                // Code generation logic or use default
                client.setCode(request.getClientName().toUpperCase().replaceAll("\\s+", "").substring(0,
                                Math.min(request.getClientName().length(), 6)));
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

                List<String> permissions = user.getRoles().stream()
                                .flatMap(r -> r.getPermissions().stream())
                                .map(p -> p.getCode().name())
                                .distinct()
                                .collect(Collectors.toList());

                // Generate response
                AuthResponse.UserResponse userResponse = AuthResponse.UserResponse.builder()
                                .id(user.getId())
                                .clientId(client.getId())
                                .emailId(user.getEmailId())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .statusCode(user.getStatusCode().name())
                                .stringAuthorities(authorities)
                                .permissions(permissions)
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

        @Transactional(readOnly = true)
        public AuthResponse signIn(LoginRequest request) {
                Authentication authentication;
                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getEmailId(),
                                                        request.getPassword()));
                } catch (Exception e) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
                }

                User user = userRepository.findByEmailId(request.getEmailId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "User not found!"));
                String data = user.getEmailId();

                Client client = user.getClient();

                List<String> authorities = user.getRoles().stream()
                                .map(r -> r.getCode().name())
                                .collect(Collectors.toList());

                List<String> permissions = user.getRoles().stream()
                                .flatMap(r -> r.getPermissions().stream())
                                .map(p -> p.getCode().name())
                                .distinct()
                                .collect(Collectors.toList());
                if (permissions.isEmpty()) {
                        permissions = List.of("USER_CREATE", "USER_READ", "USER_UPDATE", "USER_DELETE",
                                        "CLIENT_READ", "CLIENT_UPDATE", "DEALER_CREATE", "DEALER_READ", "DEALER_UPDATE",
                                        "DEALER_DELETE");
                }

                AuthResponse.UserResponse userResponse = AuthResponse.UserResponse.builder()
                                .id(user.getId())
                                .clientId(client.getId())
                                .emailId(user.getEmailId())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .statusCode(user.getStatusCode().name())
                                .stringAuthorities(authorities)
                                .permissions(permissions)
                                .build();

                AuthResponse.ClientResponse clientResponse = AuthResponse.ClientResponse.builder()
                                .id(client.getId())
                                .name(client.getName())
                                .code(client.getCode())
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

        public Boolean forgotPassword(PasswordRequest request) {
                User user = userRepository.findByEmailId(request.getEmailId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

                if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Please enter valid password.");
                }

                if (request.getPassword().equals(request.getNewPassword())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
                }

                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);

                return true;
        }
}
