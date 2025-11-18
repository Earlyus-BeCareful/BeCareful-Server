package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.common.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;

public record SocialWorkerChatRoomSummaryResponse(
        Long chatRoomId,
        Long caregiverId,
        String caregiverProfileImageUrl,
        String caregiverName,
        String elderlyName,
        int elderlyAge,
        Gender elderlyGender,
        String recentChat,
        String lastSendTime,
        Integer unreadCount,
        boolean isContractAccepted) {

    public static SocialWorkerChatRoomSummaryResponse of(
            ChatRoom chatRoom,
            Caregiver caregiver,
            Elderly elderly,
            String recentChat,
            String lastSendTime,
            Integer unreadCount,
            boolean isContractAccepted) {
        return new SocialWorkerChatRoomSummaryResponse(
                chatRoom.getId(),
                caregiver.getId(),
                caregiver.getProfileImageUrl(),
                caregiver.getName(),
                elderly.getName(),
                elderly.getAge(),
                elderly.getGender(),
                recentChat,
                lastSendTime,
                unreadCount,
                isContractAccepted);
    }
}
