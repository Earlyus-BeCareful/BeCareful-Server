package com.becareful.becarefulserver.domain.chat.controller;

import com.becareful.becarefulserver.domain.chat.dto.request.ConfirmContractRequest;
import com.becareful.becarefulserver.domain.chat.dto.request.CaregiverSendTextChatRequest;
import com.becareful.becarefulserver.domain.chat.dto.response.CaregiverChatroomResponse;
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
    @GetMapping("/list")
    public ResponseEntity<List<CaregiverChatroomResponse>> getChatInfoList() {
        var response = caregiverChatService.getChatList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양보호사 채팅방 입장 & 채팅 조회", description = "채팅방 데이터 (어르신 정보, 채팅) 반환")
    @GetMapping
    public ResponseEntity<ChatRoomDetailResponse> getChatRoomData(@RequestParam(name = "chatRoomId") Long chatRoomId) {
        var response = caregiverChatService.getChatRoomDetail(chatRoomId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary =  "요양보호사 텍스트 전송")
    @GetMapping("/send")
    public ResponseEntity<Void> createTextChat(CaregiverSendTextChatRequest request) {
        caregiverChatService.saveTextChat(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "계약서를 기반으로 매칭을 확정합니다.")
    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmMatching(ConfirmContractRequest request) {
        caregiverChatService.createCompletedMatching(request);
        return ResponseEntity.ok().build();
    }
}
