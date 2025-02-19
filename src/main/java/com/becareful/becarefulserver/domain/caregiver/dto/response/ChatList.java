package com.becareful.becarefulserver.domain.caregiver.dto.response;

import java.util.List;

public record ChatList(
        List<ChatroomInfo> chatroomInfoList
) {

    public record ChatroomInfo(
            Long matchingId,
            String nursingInstitutionName,
            String recentChat,
            String time

    ){

    }
}
