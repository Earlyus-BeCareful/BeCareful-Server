package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.ChatMessage;
import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;
import com.becareful.becarefulserver.domain.chat.dto.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.dto.*;
import com.becareful.becarefulserver.domain.nursing_institution.dto.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import java.util.*;

public record ChatRoomDetailResponse(
        Long chatRoomId, Long recruitmentId, ElderlySimpleDto elderlyInfo, List<ChatMessageDto> messages) {

    public static ChatRoomDetailResponse of(ChatRoom chatRoom, List<ChatMessage> messages) {
        Recruitment recruitment = chatRoom.getMatching().getRecruitment();
        Elderly elderly = recruitment.getElderly();

        return new ChatRoomDetailResponse(
                chatRoom.getId(),
                recruitment.getId(),
                ElderlySimpleDto.from(elderly),
                messages.stream().map(ChatMessageDto::from).toList());
    }
}
