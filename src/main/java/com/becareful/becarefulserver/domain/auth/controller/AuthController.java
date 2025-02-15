package com.becareful.becarefulserver.domain.auth.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.becareful.becarefulserver.domain.auth.dto.request.LoginRequest;
import com.becareful.becarefulserver.domain.auth.dto.response.LoginResponse;
import com.becareful.becarefulserver.domain.auth.service.LoginService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final LoginService loginService;

    @PostMapping("/caregiver/login")
    public ResponseEntity<LoginResponse> caregiverLogin(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = loginService.loginCaregiver(request);
        return ResponseEntity.ok(loginResponse);
    }
}
