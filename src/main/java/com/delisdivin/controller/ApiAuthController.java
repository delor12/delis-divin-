package com.delisdivin.controller;

import com.delisdivin.dto.*;
import com.delisdivin.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            jakarta.servlet.http.HttpServletResponse response) {
        AuthResponse authResponse = userService.login(request);
        
        // Add JWT cookie in response headers for server-side page authorization
        String cookieHeader = String.format("jwt=%s; Path=/; Max-Age=%d; SameSite=Lax; Secure", 
                authResponse.getToken(), 24 * 60 * 60);
        response.addHeader("Set-Cookie", cookieHeader);
        
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        UserDTO response = userService.register(request);
        return ResponseEntity.ok(response);
    }
}
