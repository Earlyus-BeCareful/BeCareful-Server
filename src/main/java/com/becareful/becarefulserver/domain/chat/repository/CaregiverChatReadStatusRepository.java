package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

public interface CaregiverChatReadStatusRepository extends JpaRepository<CaregiverChatReadStatus, Long> {
    @Query(
            """
        SELECT COUNT(r) > 0
          FROM CaregiverChatReadStatus cs
          JOIN cs.chatRoom r
          JOIN ChatMessage m ON m.chatRoom = r
         WHERE r.matching.workApplication.caregiver = :caregiver
           AND m.seq >= (
                   SELECT max(m2.seq)
                     FROM ChatMessage m2
                    WHERE m2.chatRoom = r
               )
           AND m.seq > cs.lastReadSeq
        """)
    boolean existsUnreadContract(Caregiver caregiver);

    Optional<CaregiverChatReadStatus> findByChatRoom(ChatRoom chatRoom);
}
