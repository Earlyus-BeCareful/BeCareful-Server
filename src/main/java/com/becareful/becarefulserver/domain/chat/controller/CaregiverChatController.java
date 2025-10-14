package com.becareful.becarefulserver.domain.chat.controller;

import com.becareful.becarefulserver.domain.chat.dto.response.CaregiverChatRoomResponse;
import com.becareful.becarefulserver.domain.chat.dto.response.ChatRoomDetailResponse;
import com.becareful.becarefulserver.domain.chat.service.CaregiverChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Caregiver Chat")
@RequestMapping("/chat/caregiver")
public class CaregiverChatController {

    private final CaregiverChatService caregiverChatService;

    @Operation(summary = "요양보호사 채팅 목록")
    @GetMapping("/room/list")
    public ResponseEntity<List<CaregiverChatRoomResponse>> getChatRoomList() {
        var response = caregiverChatService.getChatRoomList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양보호사 채팅 데이터 조회", description = "채팅방 데이터 (어르신 정보, 계약서 리스트) 반환")
    @GetMapping("/room/{chatRoomId}")
    public ResponseEntity<ChatRoomDetailResponse> getChatRoomData(@PathVariable Long chatRoomId) {
        var response = caregiverChatService.getChatRoomData(chatRoomId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "계약서를 기반으로 매칭을 확정합니다.")
    @PostMapping("/contract/{contractId}/confirm")
    public ResponseEntity<Void> confirmMatching(@PathVariable Long contractId) {
        caregiverChatService.createCompletedMatching(contractId);
        return ResponseEntity.ok().build();
    }
}
