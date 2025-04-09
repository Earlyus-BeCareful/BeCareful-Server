package com.becareful.becarefulserver.domain.auth.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.becareful.becarefulserver.domain.auth.dto.request.LoginRequest;
import com.becareful.becarefulserver.domain.auth.dto.response.LoginResponse;
import com.becareful.becarefulserver.domain.auth.service.LoginService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;

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

    @PostMapping("/socialworker/login")
    @Operation(summary = "사회복지사 로그인", description = "사회복지사 로그인 API 입니다.")
    public ResponseEntity<LoginResponse> socialWorkerLogin(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = loginService.loginSocialWorker(request);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/test")
    public ResponseEntity<Void> test() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://apis.data.go.kr/B550928/getLtcInsttDetailInfoService02/getGeneralSttusDetailInfoItem02?longTermAdminSym=21130500195&adminPttnCd=B03&serviceKey=NFmbHEQOQzmLlBw00G8LttZ8pdDGwOUsWYqDNMlBDdY747/e1Xx/gDUWTaj/1aNHSitA1BLohYKoZNn8J0ZKcw==";
        String forObject = restTemplate.getForObject(url, String.class);
        System.out.println(forObject);
        return ResponseEntity.ok().build();
    }
}
