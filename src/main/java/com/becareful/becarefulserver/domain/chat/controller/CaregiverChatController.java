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
@Tag(name = "Caregiver Chat")
@RequestMapping("/chat/caregiver")
public class CaregiverChatController {

    private final CaregiverChatService caregiverChatService;
    private final ReportService reportService;

    @Operation(summary = "요양보호사 채팅 목록")
    @GetMapping("/list")
    public ResponseEntity<List<CaregiverChatRoomSummaryResponse>> getChatInfoList() {
        var response = caregiverChatService.getChatRoomList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양보호사 채팅방 입장 & 채팅 조회", description = "채팅방 데이터 (어르신 정보, 채팅) 반환")
    @GetMapping
    public ResponseEntity<CaregiverChatRoomDetailResponse> getChatRoomData(@RequestParam Long chatRoomId) {
        var response = caregiverChatService.getChatRoomDetail(chatRoomId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양보호사 미열람채팅 알림")
    @GetMapping("/has-new-chat")
    public ResponseEntity<Boolean> hasNewChat() {
        boolean response = caregiverChatService.hasNewChat();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양보호사 채팅방 신고")
    @PostMapping("/{chatRoomId}/report")
    public ResponseEntity<Void> reportChatRoom(
            @PathVariable Long chatRoomId, @RequestBody @Valid ReportCreateRequest reportCreateRequest) {
        reportService.reportChatRoomByCaregiver(chatRoomId, reportCreateRequest);
        return ResponseEntity.ok().build();
    }
}
