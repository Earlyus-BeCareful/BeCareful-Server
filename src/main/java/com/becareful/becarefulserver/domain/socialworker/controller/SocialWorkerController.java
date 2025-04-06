package com.becareful.becarefulserver.domain.socialworker.controller;

import com.becareful.becarefulserver.domain.matching.service.ContractService;
import com.becareful.becarefulserver.domain.socialworker.dto.request.SocialworkerCreateRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.response.SocialWorkerHomeResponse;
import com.becareful.becarefulserver.domain.socialworker.dto.response.ChatList;
import com.becareful.becarefulserver.domain.socialworker.service.SocialworkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    private final SocialworkerService socialworkerService;
    private final ContractService contractService;

    @Operation(summary = "사회복지사 회원가입", description = "요양기관 ID 필수")
    @PostMapping("/signup")
    public ResponseEntity<Void> createSocialworker(@Valid @RequestBody SocialworkerCreateRequest request){
        Long id = socialworkerService.saveSocialworker(request);
        return ResponseEntity.created(URI.create("/socialworker/" + id)).build();
    }

    @Operation(summary = "채용하기", description = "근무 시작일 선택 후 근무조건 생성")
    @PostMapping("/matching/{matchingId}/hire")
    public ResponseEntity<Void> createContract(@PathVariable("matchingId") Long matchingId, @RequestParam LocalDate workStartDate) {
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
    public ResponseEntity<ChatList> getChatInfoList(){
        ChatList response = socialworkerService.getChatList();
        return ResponseEntity.ok(response);
    }
}
