package com.becareful.becarefulserver.domain.caregiver.controller;

import com.becareful.becarefulserver.domain.caregiver.dto.response.ChatroomResponse;
import com.becareful.becarefulserver.domain.caregiver.service.CaregiverChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Caregiver Chat")
@RequestMapping("/caregiver/chat")
public class CaregiverChat {

    private final CaregiverChatService caregiverChatService;

    @Operation(summary = "요양보호사 채팅 목록")
    @GetMapping("/list")
    public ResponseEntity<List<ChatroomResponse>> getChatInfoList() {
        var response = caregiverChatService.getChatList();
        return ResponseEntity.ok(response);
    }
}
