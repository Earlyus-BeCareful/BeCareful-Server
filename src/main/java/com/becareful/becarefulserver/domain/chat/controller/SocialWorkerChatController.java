package com.becareful.becarefulserver.domain.chat.controller;

import com.becareful.becarefulserver.domain.chat.dto.response.*;
import com.becareful.becarefulserver.domain.chat.service.*;
import com.becareful.becarefulserver.domain.report.dto.request.ReportCreateRequest;
import com.becareful.becarefulserver.domain.report.service.ReportService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.Valid;
import java.util.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Social Worker Chat")
@RequestMapping("/chat/social-worker")
public class SocialWorkerChatController {

    private final SocialWorkerChatService socialWorkerChatService;
    private final ReportService reportService;

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

    @Operation(summary = "계약서 상세 내용 반환", description = "계약서 수정시 이전 조건 내용을 불러오는데 사용하는 API")
    @GetMapping("/contract/{contractId}")
    public ResponseEntity<ContractDetailResponse> getContractDetail(@PathVariable Long contractId) {
        var response = socialWorkerChatService.getContractDetail(contractId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사회복지사 미열람채팅 알림")
    @GetMapping("/has-new-chat")
    public ResponseEntity<Boolean> hasNewChat() {
        boolean response = socialWorkerChatService.hasNewChat();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사회복지사 채팅방 신고")
    @PostMapping("/{chatRoomId}/report")
    public ResponseEntity<Void> reportChatRoom(
            @PathVariable Long chatRoomId, @RequestBody @Valid ReportCreateRequest request) {
        reportService.reportChatRoomBySocialWorker(chatRoomId, request);
        return ResponseEntity.ok().build();
    }
}
