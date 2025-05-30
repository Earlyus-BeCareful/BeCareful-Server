package com.becareful.becarefulserver.domain.socialworker.dto.response;

import com.becareful.becarefulserver.domain.common.vo.Gender;
import java.util.List;

public record ChatList(List<ChatroomInfo> chatroomInfoList) {

    public record ChatroomInfo(
            Long matchingId,
            String imageUrl,
            String caregiverName,
            String recentChat,
            String time,
            String elderlyName,
            int elderlyAge,
            Gender gender) {}
}
