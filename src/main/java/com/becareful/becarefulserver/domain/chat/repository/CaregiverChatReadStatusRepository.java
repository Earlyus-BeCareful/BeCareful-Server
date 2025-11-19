package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

public interface CaregiverChatReadStatusRepository extends JpaRepository<CaregiverChatReadStatus, Long> {
    @Query(
            """
        SELECT count(c) > 0
          FROM Chat c
          JOIN CaregiverChatReadStatus r
            ON r.chatRoom = c.chatRoom
         WHERE r.caregiver = :caregiver
           AND c.createDate > r.lastReadAt
    """)
    boolean existsUnreadChat(Caregiver caregiver);

    Optional<CaregiverChatReadStatus> findByChatRoom(ChatRoom chatRoom);

    List<CaregiverChatReadStatus> findAllByCaregiver(Caregiver caregiver);

    Optional<CaregiverChatReadStatus> findByChatRoomId(Long chatRoomId);

    Optional<CaregiverChatReadStatus> findByCaregiverAndChatRoom(Caregiver caregiver, ChatRoom chatRoom);
}
