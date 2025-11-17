package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

public interface SocialWorkerChatReadStatusRepository extends JpaRepository<SocialWorkerChatReadStatus, Long> {

    @Query(
            value =
                    """
    SELECT EXISTS (
        SELECT 1
        FROM chat c
        JOIN social_worker_chat_read_status r
            ON r.chat_room_id = c.chat_room_id
        WHERE r.social_worker_id = :socialWorkerId
          AND c.create_date > r.last_read_at
    )
""",
            nativeQuery = true)
    boolean existsUnreadChat(@Param("socialWorkerId") Long socialWorkerId);

    List<SocialWorkerChatReadStatus> findAllBySocialWorker(SocialWorker socialWorker);

    List<SocialWorkerChatReadStatus> chatRoom(ChatRoom chatRoom);

    SocialWorkerChatReadStatus findByChatRoomIdAndSocialWorkerId(Long chatRoomId, Long socialWorkerId);
}
