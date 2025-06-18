package com.becareful.becarefulserver.domain.auth.controller;

import com.becareful.becarefulserver.domain.auth.dto.response.OAuth2LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "인증 관련 API 입니다.")
public class AuthController {

    private final RedisTemplate<String, OAuth2LoginResponse> oauth2RedisTemplate;

    public AuthController(
            @Qualifier("oAuth2LoginResponseRedisTemplate") RedisTemplate<String, OAuth2LoginResponse> oauth2RedisTemplate) {
        this.oauth2RedisTemplate = oauth2RedisTemplate;
    }

    @Operation(summary = "회원가입 전 사용자 정보 반환", description = "카카오 소셜 로그인 완료 후 받은 key로 사용자 정보 요청")
    @GetMapping("/guest-info")
    public ResponseEntity<?> getGuestInfo(@RequestParam String guestKey) {
        String redisKey = "guest:" + guestKey;
        OAuth2LoginResponse loginInfo = oauth2RedisTemplate.opsForValue().get(redisKey);

        if (loginInfo == null) {
            return ResponseEntity.status(HttpStatus.GONE).body("만료되었거나 존재하지 않는 guestKey입니다.");
        }

        // 1회성 조회 후 삭제
        oauth2RedisTemplate.delete(redisKey);
        return ResponseEntity.ok(loginInfo);
    }
}
