package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

public interface SocialWorkerChatReadStatusRepository extends JpaRepository<SocialWorkerChatReadStatus, Long> {

    @Query(
            """
    SELECT count(*) > 0
      FROM Chat c
      JOIN SocialWorkerChatReadStatus r
        ON r.chatRoom = c.chatRoom
     WHERE r.socialWorker = :socialWorker
       AND c.createDate > r.lastReadAt
    """)
    boolean existsUnreadChat(SocialWorker socialWorker);

    List<SocialWorkerChatReadStatus> findAllBySocialWorker(SocialWorker socialWorker);

    SocialWorkerChatReadStatus findByChatRoomIdAndSocialWorkerId(Long chatRoomId, Long socialWorkerId);

    Optional<SocialWorkerChatReadStatus> findBySocialWorkerAndChatRoom(SocialWorker sw, ChatRoom chatRoom);
}
