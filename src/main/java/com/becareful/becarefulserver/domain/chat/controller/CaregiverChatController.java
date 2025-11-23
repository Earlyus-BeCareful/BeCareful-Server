package com.becareful.becarefulserver.domain.chat.controller;

import com.becareful.becarefulserver.domain.chat.dto.response.*;
import com.becareful.becarefulserver.domain.chat.service.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
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
}
