package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

public interface CaregiverChatReadStatusRepository extends JpaRepository<CaregiverChatReadStatus, Long> {
    @Query(
            """
        SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END
        FROM CaregiverChatReadStatus cs
        JOIN cs.matching m
        JOIN Contract c ON c.matching = m
        WHERE cs.caregiver = :caregiver
            AND c.createDate > cs.lastReadAt
        """)
    boolean existsUnreadContract(@Param("caregiver") Caregiver caregiver);

    Optional<CaregiverChatReadStatus> findByCaregiverAndMatching(Caregiver caregiver, Matching matching);

    void deleteAllByMatchingIn(List<Matching> matchings);
}
