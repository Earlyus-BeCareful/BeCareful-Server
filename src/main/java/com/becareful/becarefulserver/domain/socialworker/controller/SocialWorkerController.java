package com.becareful.becarefulserver.domain.socialworker.controller;

import com.becareful.becarefulserver.domain.socialworker.dto.request.*;
import com.becareful.becarefulserver.domain.socialworker.dto.response.*;
import com.becareful.becarefulserver.domain.socialworker.service.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.servlet.http.*;
import jakarta.validation.*;
import java.net.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/socialworker")
@Tag(name = "Social Worker", description = "사회복지사 관련 API 입니다.")
public class SocialWorkerController {

    private final SocialWorkerService socialworkerService;

    @Operation(summary = "사회복지사 회원가입", description = "센터장, 대표, 사회복지사 모두 같은 API")
    @PostMapping("/signup")
    public ResponseEntity<Void> createSocialWorker(
            @Valid @RequestBody SocialWorkerCreateRequest request, HttpServletResponse response) {
        Long id = socialworkerService.createSocialWorker(request, response);
        return ResponseEntity.created(URI.create("/socialworker/" + id)).build();
    }

    @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복 확인 API. 동일한 닉네임이 있다면 true 반환")
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        boolean sameNickName = socialworkerService.checkSameNickNameAtRegist(nickname);
        return ResponseEntity.ok(sameNickName);
    }

    @Operation(summary = "사회복지사 홈화면 조회")
    @GetMapping("/home")
    public ResponseEntity<SocialWorkerHomeResponse> getHomeData() {
        var response = socialworkerService.getHomeData();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원정보 반환", description = "센터장, 대표, 사회복지사 모두 같은 API")
    @GetMapping("/me")
    public ResponseEntity<SocialWorkerMyResponse> getSocialWorkerMyInfo() {
        var response = socialworkerService.getMyInfo();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원정보 수정화면 정보 반환", description = "센터장, 대표, 사회복지사 모두 같은 API")
    @GetMapping("/me/edit")
    public ResponseEntity<SocialWorkerEditResponse> getSocialWorkerEditInfo() {
        var response = socialworkerService.getEditMyInfo();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원정보 수정", description = "센터장, 대표, 사회복지사 모두 같은 API")
    @PutMapping("/me")
    public ResponseEntity<Void> updateMyBasicInfo(
            @Valid @RequestBody SocialWorkerUpdateBasicInfoRequest request, HttpServletResponse httpServletResponse) {
        socialworkerService.updateMyBasicInfo(request, httpServletResponse);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그아웃")
    @PutMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse httpServletResponse) {
        socialworkerService.logout(httpServletResponse);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "탈퇴")
    @DeleteMapping("/leave")
    public ResponseEntity<Void> leave(HttpServletResponse httpServletResponse) {
        socialworkerService.leave(httpServletResponse);
        return ResponseEntity.ok().build();
    }
}
