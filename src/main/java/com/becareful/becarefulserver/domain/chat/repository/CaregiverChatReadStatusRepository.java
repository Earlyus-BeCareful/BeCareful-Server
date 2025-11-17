package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

public interface CaregiverChatReadStatusRepository extends JpaRepository<CaregiverChatReadStatus, Long> {
    @Query(
            value =
                    """
    SELECT EXISTS (
        SELECT 1
        FROM chat c
        JOIN caregiver_chat_read_status r
            ON r.chat_room_id = c.chat_room_id
        WHERE r.caregiver_id = :caregiverId
          AND c.create_date > r.last_read_at
    )
    """,
            nativeQuery = true)
    boolean existsUnreadChat(@Param("caregiver") Long caregiverId);

    Optional<CaregiverChatReadStatus> findByChatRoom(ChatRoom chatRoom);

    List<CaregiverChatReadStatus> findAllByCaregiver(Caregiver caregiver);

    Optional<CaregiverChatReadStatus> findByChatRoomId(Long chatRoomId);
}
