package com.becareful.becarefulserver.domain.chat.controller;

import com.becareful.becarefulserver.domain.chat.dto.request.*;
import com.becareful.becarefulserver.domain.chat.dto.response.ContractDetailResponse;
import com.becareful.becarefulserver.domain.chat.dto.response.SocialWorkerChatRoomDetailResponse;
import com.becareful.becarefulserver.domain.chat.dto.response.SocialWorkerChatRoomSummaryResponse;
import com.becareful.becarefulserver.domain.chat.service.SocialWorkerChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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
    public ResponseEntity<List<SocialWorkerChatRoomSummaryResponse>> getChatInfoList() {
        var response = socialWorkerChatService.getChatList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사회복지사 채팅 데이터 조회", description = "채팅방 데이터 (어르신 정보, 계약서 리스트) 반환")
    @GetMapping
    public ResponseEntity<SocialWorkerChatRoomDetailResponse> getChatRoomData(@RequestParam Long chatRoomId) {
        var response = socialWorkerChatService.getChatRoomDetailData(chatRoomId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사회복지사 텍스트 채팅 전송")
    @PostMapping("/send")
    public ResponseEntity<Void> createTextChat(SocialWorkerSendTextChatRequest request) {
        socialWorkerChatService.createTextChat(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "계약서 상세 내용 반환", description = "계약서 수정시 이전 조건 내용을 불러오는데 사용하는 API")
    @GetMapping("/contract/{contractId}")
    public ResponseEntity<ContractDetailResponse> getContractDetail(@PathVariable Long contractId) {
        var response = socialWorkerChatService.getContractDetail(contractId);
        return ResponseEntity.ok(response);
    }

    // 계약서 수정 내용 저장 - 직전 계약서 필요
    @Operation(summary = "수정 계약서 생성")
    @PostMapping("/contract/edit")
    public ResponseEntity<Void> editContract(@RequestBody @Valid ContractEditRequest request) {
        Long contractId = socialWorkerChatService.editContract(request);
        return ResponseEntity.created(URI.create("/contract/" + contractId)).build();
    }

    // TODO: 채용 확정
    @Operation(summary = "계약서를 기반으로 매칭을 확정합니다.")
    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmMatching(ConfirmContractRequest request) {
        socialWorkerChatService.createCompletedMatching(request);
        return ResponseEntity.ok().build();
    }
}
