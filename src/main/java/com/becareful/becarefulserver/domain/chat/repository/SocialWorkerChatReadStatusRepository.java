package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

public interface SocialWorkerChatReadStatusRepository extends JpaRepository<SocialWorkerChatReadStatus, Long> {

    Optional<SocialWorkerChatReadStatus> findBySocialWorkerAndMatching(
            SocialWorker loggedInSocialWorker, Matching matching);

    @Query(
            """
        SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END
        FROM SocialWorkerChatReadStatus s
        JOIN s.matching m
        JOIN Contract c ON c.matching = m
        WHERE s.socialWorker = :socialWorker
            AND c.createDate > s.lastReadAt
        """)
    boolean existsUnreadContract(@Param("socialWorker") SocialWorker socialWorker);
}
