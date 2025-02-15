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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "인증 관련 API 입니다.")
public class AuthController {

    private final LoginService loginService;

    @PostMapping("/caregiver/login")
    @Operation(summary = "요양복지사 로그인", description = "요양복지사 로그인 API 입니다.")
    public ResponseEntity<LoginResponse> caregiverLogin(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = loginService.loginCaregiver(request);
        return ResponseEntity.ok(loginResponse);
    }
}
