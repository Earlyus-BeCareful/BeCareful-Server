package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import java.util.*;

public record SocialWorkerChatRoomDetailResponse(
        String caregiverName,
        String caregiverProfileImageUrl,
        String elderlyName,
        String elderlyProfileImageUrl,
        ChatRoomActivateStatus chatRoomStatus,
        ChatRoomContractStatus chatRoomContractStatus,
        long recruitmentId,
        List<ChatResponseDto> chatList) {

    public static SocialWorkerChatRoomDetailResponse of(Recruitment recruitment, Caregiver caregiver, Elderly elderly, ChatRoom chatRoom, List<ChatResponseDto> chatList) {
        return new SocialWorkerChatRoomDetailResponse(
                caregiver.getName(),
                caregiver.getProfileImageUrl(),
                elderly.getName(),
                elderly.getProfileImageUrl(),
                chatRoom.getChatRoomActivateStatus(),
                chatRoom.getChatRoomContractStatus(),
                recruitment.getId(),
                chatList
        );
    }
}
