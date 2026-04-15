package com.ritfast.processor.controller;

import com.ritfast.processor.dto.AuthResponse;
import com.ritfast.processor.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/loggedInClient")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<AuthResponse> loggedInClient() {
        AuthResponse response = clientService.loggedInClient();
        return ResponseEntity.ok(response);
    }

}
