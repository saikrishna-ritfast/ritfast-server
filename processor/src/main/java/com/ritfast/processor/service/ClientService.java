package com.ritfast.processor.service;

import com.ritfast.processor.dto.AuthResponse;
import com.ritfast.processor.entity.Client;
import com.ritfast.processor.entity.User;
import com.ritfast.processor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public AuthResponse loggedInClient() {

        // 1. Get the current authenticated user from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        // 2. Extract email from the principal
        String email;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        // 3. Look up the User entity
        User user = userRepository.findByEmailId(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 4. Get the associated Client
        Client client = user.getClient();

        // 5. Build authorities list from roles
        List<String> authorities = user.getRoles().stream()
                .map(r -> r.getCode().name())
                .collect(Collectors.toList());

        // 6. Build UserResponse
        AuthResponse.UserResponse userResponse = AuthResponse.UserResponse.builder()
                .id(user.getId())
                .clientId(client.getId())
                .emailId(user.getEmailId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .statusCode(user.getStatusCode().name())
                .stringAuthorities(authorities)
                .build();

        // 7. Build ClientResponse
        AuthResponse.ClientResponse clientResponse = AuthResponse.ClientResponse.builder()
                .id(client.getId())
                .code(client.getCode())
                .name(client.getName())
                .typeCode(client.getTypeCode())
                .levelType(client.getLevelType().name())
                .statusCode(client.getStatusCode().name())
                .build();

        // 8. Return the full AuthResponse
        return AuthResponse.builder()
                .user(userResponse)
                .client(clientResponse)
                .loggedInClientId(client.getId())
                .loggedInClientCode(client.getCode())
                .build();
    }
}
