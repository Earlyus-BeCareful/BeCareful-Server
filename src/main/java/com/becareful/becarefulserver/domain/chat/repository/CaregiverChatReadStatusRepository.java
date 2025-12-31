package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
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

    @Query(
            """
        SELECT s.chatRoom.id
          FROM CaregiverChatReadStatus s
         WHERE s.caregiver = :caregiver
           AND s.chatRoom.recruitment = :recruitment
    """)
    Optional<Long> findChatRoomIdByCaregiverAndRecruitment(Caregiver caregiver, Recruitment recruitment);

    Optional<CaregiverChatReadStatus> findByChatRoom(ChatRoom chatRoom);

    List<CaregiverChatReadStatus> findAllByCaregiver(Caregiver caregiver);

    Optional<CaregiverChatReadStatus> findByChatRoomId(Long chatRoomId);

    Optional<CaregiverChatReadStatus> findByCaregiverAndChatRoom(Caregiver caregiver, ChatRoom chatRoom);
}
