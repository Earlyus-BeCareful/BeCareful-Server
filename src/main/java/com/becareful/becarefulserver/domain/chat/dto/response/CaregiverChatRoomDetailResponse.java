package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.chat.dto.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import java.util.*;

public record CaregiverChatRoomDetailResponse(
        String institutionName,
        String institutionProfileImageUrl,
        String elderlyName,
        String elderlyProfileImageUrl,
        ChatRoomActiveStatus chatRoomStatus,
        ChatRoomContractStatus chatRoomContractStatus,
        long recruitmentId,
        List<ChatHistoryResponseDto> chatList) {
    public static CaregiverChatRoomDetailResponse of(
            Recruitment recruitment,
            NursingInstitution institution,
            Elderly elderly,
            ChatRoom chatRoom,
            List<ChatHistoryResponseDto> chatList) {
        return new CaregiverChatRoomDetailResponse(
                institution.getName(),
                institution.getProfileImageUrl(),
                elderly.getName(),
                elderly.getProfileImageUrl(),
                chatRoom.getChatRoomActiveStatus(),
                chatRoom.getChatRoomContractStatus(),
                recruitment.getId(),
                chatList);
    }
}
