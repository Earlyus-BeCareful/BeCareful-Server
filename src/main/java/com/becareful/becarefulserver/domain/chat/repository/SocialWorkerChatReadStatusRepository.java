package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

public interface SocialWorkerChatReadStatusRepository extends JpaRepository<SocialWorkerChatReadStatus, Long> {

    Optional<SocialWorkerChatReadStatus> findBySocialWorkerAndMatching(
            SocialWorker loggedInSocialWorker, Matching matching);

    @Query(
            """
        SELECT COUNT(r) > 0
          FROM SocialWorkerChatReadStatus cs
          JOIN cs.chatRoom r
          JOIN ChatMessage m ON m.chatRoom = r
         WHERE r.matching.recruitment.elderly.nursingInstitution = :institution
           AND cs.socialWorker = :socialWorker
           AND m.seq >= (
                   SELECT max(m2.seq)
                     FROM ChatMessage m2
                    WHERE m2.chatRoom = r
               )
           AND m.seq > cs.lastReadSeq
        """)
    boolean existUnreadMessages(NursingInstitution institution, SocialWorker socialWorker);

    Optional<SocialWorkerChatReadStatus> findByChatRoom(ChatRoom chatRoom);
}
