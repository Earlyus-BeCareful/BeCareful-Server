package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findAllByChatRoomActiveStatusAndRecruitment(ChatRoomActiveStatus status, Recruitment recruitment);

    Iterable<ChatRoom> findAllByChatRoomActiveStatusAndRecruitmentId(
            ChatRoomActiveStatus chatRoomActiveStatus, Long recruitmentId);
}
