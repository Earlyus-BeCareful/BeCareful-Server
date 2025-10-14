package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.ChatMessage;
import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.dto.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.global.util.DateTimeUtil;
import java.time.*;

public record SocialWorkerChatRoomResponse(
        Long chatRoomId,
        CaregiverSimpleDto caregiverInfo,
        String recentChat,
        String lastSendTime,
        ElderlySimpleDto elderlyInfo,
        Integer unreadCount) {

    public static SocialWorkerChatRoomResponse of(ChatRoom chatRoom, ChatMessage recentMessage) {
        // String recentChat = isCompleted ? "최종 승인이 확정되었습니다!" : "합격 축하드립니다.";
        String timeDifference = DateTimeUtil.getTimeDifferenceString(recentMessage.getCreateDate());

        return new SocialWorkerChatRoomResponse(
                chatRoom.getId(),
                CaregiverSimpleDto.from(
                        chatRoom.getMatching().getWorkApplication().getCaregiver()),
                recentMessage.getContent(),
                timeDifference,
                ElderlySimpleDto.from(chatRoom.getMatching().getRecruitment().getElderly()),
                0 // TODO : unread count 구현
                );
    }
}
