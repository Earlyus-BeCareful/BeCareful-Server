package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.*;

public record CaregiverChatRoomSummaryResponse(
        Long chatRoomId,
        String nursingInstitutionProfileImageUrl,
        String nursingInstitutionName,
        String recentChat,
        String lastSendTime,
        Integer unreadCount) {

    public static CaregiverChatRoomSummaryResponse of(
            ChatRoom chatRoom,
            NursingInstitution institution,
            String recentChat,
            String lastSendTime,
            Integer unreadCount) {
        return new CaregiverChatRoomSummaryResponse(
                chatRoom.getId(),
                institution.getProfileImageUrl(),
                institution.getName(),
                recentChat,
                lastSendTime,
                unreadCount);
    }
}
