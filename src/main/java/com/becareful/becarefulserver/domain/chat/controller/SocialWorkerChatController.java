package com.becareful.becarefulserver.domain.chat.controller;

import com.becareful.becarefulserver.domain.chat.service.SocialWorkerChatService;
import com.becareful.becarefulserver.domain.matching.dto.request.ContractEditRequest;
import com.becareful.becarefulserver.domain.matching.dto.response.ContractDetailResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.ContractInfoListResponse;
import com.becareful.becarefulserver.domain.socialworker.dto.response.ChatList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Social Worker Chat")
@RequestMapping("/chat/social-worker")
public class SocialWorkerChatController {

    private final SocialWorkerChatService socialWorkerChatService;

    @Operation(summary = "사회복지사 채팅 목록")
    @GetMapping("/list")
    public ResponseEntity<ChatList> getChatInfoList() {
        ChatList response = socialWorkerChatService.getChatList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사회복지사 채팅 데이터 조회", description = "채팅방 데이터 (어르신 정보, 계약서 리스트) 반환")
    @GetMapping
    public ResponseEntity<ContractInfoListResponse> getChatRoomData(
            @RequestParam(name = "matchingId") Long matchingId) {
        var response = socialWorkerChatService.getChatRoomDetailData(matchingId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "계약서 상세 내용 반환", description = "계약서 수정시 이전 조건 내용을 불러오는데 사용하는 API")
    @GetMapping("/contract/{contractId}")
    public ResponseEntity<ContractDetailResponse> getContractDetail(@PathVariable Long contractId) {
        var response = socialWorkerChatService.getContractDetail(contractId);
        return ResponseEntity.ok(response);
    }

    // 계약서 수정 내용 저장 - 직전 계약서 필요
    @Operation(summary = "수정 계약서 생성")
    @PostMapping("/edit")
    public ResponseEntity<Void> editContract(@RequestBody ContractEditRequest request) {
        socialWorkerChatService.editContract(request);
        return ResponseEntity.ok().build();
    }
}
