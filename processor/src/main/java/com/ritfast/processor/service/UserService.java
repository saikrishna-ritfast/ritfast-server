package com.ritfast.processor.service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.ritfast.common.dto.PageResponse;
import com.ritfast.common.enums.RoleCode;
import com.ritfast.common.enums.StatusCode;
import com.ritfast.processor.entity.Role;
import com.ritfast.processor.repository.RoleRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.ritfast.processor.dto.User.UserResponse;
import com.ritfast.processor.dto.base.MetaData;
import com.ritfast.processor.dto.User.UserRequest;
import com.ritfast.processor.entity.Client;
import com.ritfast.processor.entity.User;
import com.ritfast.processor.repository.UserRepository;
import com.ritfast.processor.repository.specification.UserSpecification;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserRequest request) {
        User currentUser = new User();
        Client client = getAuthenticatedUser().getClient();

        if (request.getFirstName().isBlank() || request.getLastName().isBlank() || request.getEmailId().isBlank()
                || request.getPassword().isBlank() || request.getPhoneNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All fields are required.");
        }

        if (userRepository.findByEmailId(request.getEmailId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use.");
        }

        RoleCode roleCode = request.getRoleCode() != null ? request.getRoleCode() : RoleCode.ROLE_EMPLOYEE;
        Role role = roleRepository.findByCode(roleCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + roleCode));

        User user = new User();
        user.setEmailId(request.getEmailId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setName(request.getFirstName() + " " + request.getLastName());
        user.setClient(client);
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatusCode(StatusCode.ACTIVE);
        user.getRoles().add(role);

        user.setCreateBy(getAuthenticatedUser().getId());
        user.setUpdateBy(getAuthenticatedUser().getId());
        user.setCreateAt(java.time.LocalDateTime.now().toString());
        user.setUpdateAt(java.time.LocalDateTime.now().toString());

        user = userRepository.save(user);

        return mapToResponse(user);

    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(String search, Pageable pageable) {
        Long clientId = getAuthenticatedUser().getClient().getId();
        Specification<User> spec = UserSpecification.filterUsers(clientId, search);

        Page<User> userPage = userRepository.findAll(spec, pageable);

        List<UserResponse> content = userPage.getContent().stream()
                .map(this::mapToResponseWithOutPermission)
                .collect(Collectors.toList());

        return PageResponse.<UserResponse>builder()
                .content(content)
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .build();
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authentication");
        }
        String email;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        return userRepository.findByEmailId(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Authenticated user not found"));
    }

    private UserResponse mapToResponse(User user) {
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getCode().name())
                .collect(Collectors.toList());

        List<String> permissions = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> p.getCode().name())
                .distinct()
                .collect(Collectors.toList());

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .clientId(user.getClient().getId())
                .emailId(user.getEmailId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .name(user.getFirstName() + " " + user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .statusCode(user.getStatusCode().name())
                .roles(roles)
                .permissions(permissions)
                .build();

        response.setCreateBy(user.getCreateBy());
        response.setCreateAt(user.getCreateAt());
        response.setUpdateBy(user.getUpdateBy());
        response.setUpdateAt(user.getUpdateAt());

        return response;
    }

    private UserResponse mapToResponseWithOutPermission(User user) {
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getCode().name())
                .collect(Collectors.toList());

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .clientId(user.getClient().getId())
                .emailId(user.getEmailId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .name(user.getFirstName() + " " + user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .statusCode(user.getStatusCode().name())
                .roles(roles)
                .build();

        response.setCreateBy(user.getCreateBy());
        response.setCreateAt(user.getCreateAt());
        response.setUpdateBy(user.getUpdateBy());
        response.setUpdateAt(user.getUpdateAt());

        return response;
    }
}