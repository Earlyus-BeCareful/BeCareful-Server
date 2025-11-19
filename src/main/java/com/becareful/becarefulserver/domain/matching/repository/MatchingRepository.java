package com.becareful.becarefulserver.domain.matching.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    @Query(
            """
    SELECT m
      FROM Matching m
      JOIN m.workApplication w
     WHERE w.caregiver = :caregiver
       AND m.matchingStatus = :applicationStatus
    """)
    List<Matching> findAllByCaregiverAndApplicationStatus(Caregiver caregiver, MatchingStatus applicationStatus);

    @Modifying
    @Query(
            """
    DELETE
      FROM Matching m
     WHERE m.workApplication = :application
       AND m.matchingStatus = :matchingStatus
    """)
    void deleteAllByApplicationAndMatchingStatus(WorkApplication application, MatchingStatus matchingStatus);

    Optional<Matching> findByWorkApplicationAndRecruitment(WorkApplication workApplication, Recruitment recruitment);

    @Query(
            """
                SELECT m
                  FROM Matching m
                  JOIN FETCH m.workApplication w
                  JOIN FETCH w.caregiver c
                  JOIN FETCH m.recruitment r
                 WHERE c.id = :caregiverId
                   AND r.id = :recruitmentId
    """)
    Optional<Matching> findByCaregiverIdAndRecruitmentId(Long caregiverId, Long recruitmentId);

    List<Matching> findByWorkApplicationAndMatchingStatus(
            WorkApplication workApplication, MatchingStatus matchingStatus);

    @Modifying
    @Query("DELETE FROM Matching m WHERE m.workApplication.caregiver = :caregiver AND m.matchingStatus <> :status")
    void deleteAllByCaregiverAndStatusNot(
            @Param("caregiver") Caregiver caregiver, @Param("status") MatchingStatus status);

    List<Matching> findAllByMatchingStatusAndRecruitment(MatchingStatus matchingStatus, Recruitment recruitment);
}
