package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.chat.domain.ChatMessage;
import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query(
            """
        SELECT m
          FROM ChatMessage m
         WHERE m.chatRoom = :room
         ORDER BY m.createDate
         LIMIT 1
    """)
    Optional<ChatMessage> findRecentMessageByChatRoom(ChatRoom room);

    List<ChatMessage> findAllByChatRoomOrderBySeqDesc(ChatRoom chatRoom);
}
