package com.iagro.pettersson.security;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/login")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest dto) {
        LoginResponse response = authService.login(dto);
        return ResponseEntity.ok(response);
    }
}
