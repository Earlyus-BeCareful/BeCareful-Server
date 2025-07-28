package com.becareful.becarefulserver.domain.test.controller;

import com.becareful.becarefulserver.domain.test.service.TestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "TEST API", description = "개발 환경에서만 사용하는 테스트 API 입니다.")
@RequestMapping("/test")
public class TestController {

    private final TestService testService;

    @DeleteMapping("/social-worker")
    public ResponseEntity<Void> deleteSocialWorker(@RequestParam String phoneNumber) {
        testService.deleteSocialWorker(phoneNumber);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/caregiver")
    public ResponseEntity<Void> deleteCaregiver(@RequestParam String phoneNumber) {
        testService.deleteCaregiver(phoneNumber);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/token-setting")
    public ResponseEntity<Void> tokenSetting(@RequestParam String phoneNumber, HttpServletResponse response) {
        testService.tokenSetting(phoneNumber, response);
        return ResponseEntity.ok().build();
    }
}
