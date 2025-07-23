package com.becareful.becarefulserver.domain.socialworker.controller;

import com.becareful.becarefulserver.domain.matching.service.ContractService;
import com.becareful.becarefulserver.domain.socialworker.dto.request.SocialWorkerCreateRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.request.SocialWorkerUpdateBasicInfoRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.response.ChatList;
import com.becareful.becarefulserver.domain.socialworker.dto.response.SocialWorkerHomeResponse;
import com.becareful.becarefulserver.domain.socialworker.dto.response.SocialWorkerMyInfo;
import com.becareful.becarefulserver.domain.socialworker.service.SocialWorkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/socialworker")
@Tag(name = "Social Worker", description = "사회복지사 관련 API 입니다.")
public class SocialWorkerController {

    private final SocialWorkerService socialworkerService;
    private final ContractService contractService;

    @Operation(summary = "사회복지사 회원가입", description = "센터장, 대표, 사회복지사 모두 같은 API")
    @PostMapping("/signup")
    public ResponseEntity<Void> createSocialworker(
            @Valid @RequestBody SocialWorkerCreateRequest request, HttpServletResponse response) {
        Long id = socialworkerService.saveSocialworker(request, response);
        return ResponseEntity.created(URI.create("/socialworker/" + id)).build();
    }

    @Operation(summary = "닉네임 중복 확인", description = "회원가입시 닉네임 중복 확인 API. 동일한 닉네임이 있다면 true 반환")
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> nickNameCheck(@RequestParam String nickname) {
        boolean sameNickName = socialworkerService.checkSameNickNameAtRegist(nickname);
        return ResponseEntity.ok(sameNickName);
    }

    @Operation(summary = "회원정보 반환", description = "센터장, 대표, 사회복지사 모두 같은 API")
    @GetMapping("/me")
    public ResponseEntity<SocialWorkerMyInfo> getSocialWorkerMyInfo() {
        SocialWorkerMyInfo response = socialworkerService.getMyInfo();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원정보 수정", description = "센터장, 대표, 사회복지사 모두 같은 API")
    @PutMapping("/me")
    public ResponseEntity<Void> updateMyBasicInfo(
            @Valid @RequestBody SocialWorkerUpdateBasicInfoRequest request, HttpServletResponse httpServletResponse) {
        socialworkerService.updateMyBasicInfo(request, httpServletResponse);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채용하기", description = "근무 시작일 선택 후 근무조건 생성")
    @PostMapping("/matching/{matchingId}/hire")
    public ResponseEntity<Void> createContract(
            @PathVariable("matchingId") Long matchingId, @RequestParam LocalDate workStartDate) {
        contractService.createContract(matchingId, workStartDate);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사회복지사 홈화면 조회")
    @GetMapping("/home")
    public ResponseEntity<SocialWorkerHomeResponse> getHomeData() {
        SocialWorkerHomeResponse response = socialworkerService.getHomeData();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사회복지사 채팅 목록")
    @GetMapping("/chat/list")
    public ResponseEntity<ChatList> getChatInfoList() {
        ChatList response = socialworkerService.getChatList();
        return ResponseEntity.ok(response);
    }
}
