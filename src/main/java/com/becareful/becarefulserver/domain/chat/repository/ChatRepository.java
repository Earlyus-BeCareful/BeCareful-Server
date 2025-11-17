package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import java.time.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    int countByChatRoom(ChatRoom chatRoom);

    int countByChatRoomAndCreateDateAfter(ChatRoom chatRoom, LocalDateTime lastReadAt);

    @Query(
            """
    SELECT c
    FROM Chat c
    LEFT JOIN FETCH TextChat t ON c.id = t.id
    LEFT JOIN FETCH Contract co ON c.id = co.id
    WHERE c.chatRoom.id = :chatRoomId
    ORDER BY c.createDate DESC
    LIMIT 1
""")
    Optional<Chat> findLastChatWithContent(@Param("chatRoomId") Long chatRoomId);

    @Query("""
    SELECT c
    FROM Chat c
    WHERE c.chatRoom.id = :chatRoomId
    ORDER BY c.createDate ASC
    """)
    List<Chat> findAllChatWithContent(@Param("chatRoomId") Long chatRoomId);
}
