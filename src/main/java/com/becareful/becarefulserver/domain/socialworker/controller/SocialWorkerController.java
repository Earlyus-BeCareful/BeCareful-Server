package com.becareful.becarefulserver.domain.socialworker.controller;

import com.becareful.becarefulserver.domain.association.dto.AssociationMemberDto;
import com.becareful.becarefulserver.domain.common.dto.request.ProfileImagePresignedUrlRequest;
import com.becareful.becarefulserver.domain.common.dto.response.PresignedUrlResponse;
import com.becareful.becarefulserver.domain.socialworker.dto.SocialWorkerDto;
import com.becareful.becarefulserver.domain.socialworker.dto.request.*;
import com.becareful.becarefulserver.domain.socialworker.dto.response.*;
import com.becareful.becarefulserver.domain.socialworker.service.*;
import com.becareful.becarefulserver.global.util.AuthUtil;
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
@RequestMapping("/social-worker")
@Tag(name = "Social Worker", description = "사회복지사 관련 API 입니다.")
public class SocialWorkerController {

    private final SocialWorkerService socialWorkerService;
    private final AuthUtil authUtil;

    @Operation(summary = "사회복지사 회원가입", description = "센터장, 대표, 사회복지사 모두 같은 API")
    @PostMapping("/signup")
    public ResponseEntity<Void> createSocialWorker(@Valid @RequestBody SocialWorkerCreateRequest request) {
        Long id = socialWorkerService.createSocialWorker(request);
        return ResponseEntity.created(URI.create("/social-worker/" + id)).build();
    }

    @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복 확인 API. 동일한 닉네임이 있다면 true 반환")
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        boolean sameNickName = socialWorkerService.checkSameNickNameAtRegist(nickname);
        return ResponseEntity.ok(sameNickName);
    }

    @Operation(summary = "사회복지사 홈화면 조회")
    @GetMapping("/home")
    public ResponseEntity<SocialWorkerHomeResponse> getHomeData() {
        var response = socialWorkerService.getHomeData();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "5.1.1 사회복지사 마이페이지", description = "사회복지사 마이페이지 화면 데이터를 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<SocialWorkerMyPageResponse> getSocialWorkerMyPageData() {
        var response = socialWorkerService.getMyPageData();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "5.1.2 사회복지사 프로필 조회", description = "사회복지사 프로필 수정시 프로필 데이터를 조회합니다.")
    @GetMapping("/my/profile")
    public ResponseEntity<SocialWorkerDto> getSocialWorkerProfile() {
        var response = socialWorkerService.getMyProfile();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "5.1.2 사회복지사 프로필 수정", description = "사회복지사 프로필을 수정합니다.")
    @PutMapping("/my/profile")
    public ResponseEntity<Void> updateSocialWorkerProfile(
            @Valid @RequestBody SocialWorkerProfileUpdateRequest request) {
        socialWorkerService.updateSocialWorkerProfile(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사회복지사 설정 화면 정보 조회")
    @GetMapping("/my/setting")
    public ResponseEntity<SocialWorkerMySettingResponse> getSocialWorkerMySetting() {
        var response = socialWorkerService.getSocialWorkerMySetting();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사회복지사 마케팅 수신 정보 동의 여부 수정")
    @PatchMapping("/my/marketing-info-receiving-agreement")
    public ResponseEntity<Void> updateMyMarketingInfoReceivingAgreement(
            @Valid @RequestBody SocialWorkerMyMarketingInfoReceivingAgreementUpdateRequest request) {
        socialWorkerService.updateSocialWorkerMyMarketingInfoReceivingAgreement(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "5.1.3 사회복지사 협회 상세 정보 조회", description = "사회복지사 마이페이지에서 협회 가입 상세 정보를 조회합니다.")
    @GetMapping("/my/association")
    public ResponseEntity<AssociationMemberDto> getSocialWorkerAssociationDetail() {
        var response = socialWorkerService.getSocialWorkerMyAssociationDetail();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "5.1.3 사회복지사 협회 상세 정보 수정", description = "사회복지사 마이페이지에서 협회 가입 상세 정보를 수정합니다.")
    @PatchMapping("/my/association")
    public ResponseEntity<Void> updateSocialWorkerAssociationDetail(
            @Valid @RequestBody SocialWorkerMyAssociationDetailUpdateRequest request) {
        socialWorkerService.updateSocialWorkerMyAssociationDetail(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그아웃")
    @PutMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse httpServletResponse) {
        authUtil.logout(httpServletResponse);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사회복지사 프로필 이미지 업로드")
    @PostMapping("/profile-img/presigned-url")
    public ResponseEntity<PresignedUrlResponse> createPresignedUrl(
            @Valid @RequestBody ProfileImagePresignedUrlRequest request) {
        var response = socialWorkerService.getPresignedUrl(request);
        return ResponseEntity.ok(response);
    }

    // TODO : 탈퇴 url DELETE /social-worker 로 수정
    @Operation(summary = "탈퇴")
    @DeleteMapping("/leave")
    public ResponseEntity<Void> deleteSocialWorker(HttpServletResponse response) {
        socialWorkerService.deleteSocialWorker();
        authUtil.logout(response);
        return ResponseEntity.ok().build();
    }
}
