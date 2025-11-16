package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("""
    select c.targetId
    from Chat c
    where c.id = :chatRoomId
      and c.chatType = 'CONTRACT'
    order by c.createDate desc
    limit 1
""")
    Long findLastContractIdByChatRoomId(Long chatRoomId);


    Optional<ChatRoom> findByMatching(Matching matching);

    Optional<ChatRoom> findByMatchingIdAndChatRoomStatus(Long id, ChatRoomActivateStatus chatRoomStatus);
}
