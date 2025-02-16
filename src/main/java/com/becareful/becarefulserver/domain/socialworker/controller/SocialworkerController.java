package com.becareful.becarefulserver.domain.socialworker.controller;

import com.becareful.becarefulserver.domain.socialworker.dto.request.SocialworkerCreateRequest;
import com.becareful.becarefulserver.domain.socialworker.service.SocialworkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/socialworker")
@Tag(name = "socialworker", description = "사회복지사 관련 API 입니다.")
public class SocialworkerController {
   private final SocialworkerService socialworkerService;
    @Operation(summary = "사회복지사 회원가입", description = "요양기관 ID 필수")
    @PostMapping("/signup")
    public ResponseEntity<Void> createSocialworker(@Valid @RequestBody SocialworkerCreateRequest request){
        Long id = socialworkerService.saveSocialworker(request);
        return ResponseEntity.created(URI.create("/socialworker/" + id)).build();
    }
}
