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
    SELECT m FROM Matching m
    JOIN m.recruitment r
    JOIN r.elderly e
    WHERE e.nursingInstitution = :nursingInstitution
    AND m.matchingStatus = '합격'
    """)
    List<Matching> findAllByNursingInstitution(@Param("nursingInstitution") NursingInstitution nursingInstitution);

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

    List<Matching> findAllByRecruitment(Recruitment recruitment);

    Optional<Matching> findByWorkApplicationAndRecruitment(WorkApplication workApplication, Recruitment recruitment);

    @Query(
            "SELECT m FROM Matching m WHERE m.workApplication.caregiver = :caregiver AND m.recruitment.id = :recruitmentId")
    Optional<Matching> findByCaregiverAndRecruitmentId(Caregiver caregiver, Long recruitmentId);

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

    @Query(
            """
        SELECT m
          FROM Matching m
         WHERE m.workApplication.caregiver = :caregiver
           AND m.applicationStatus = com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus.지원
           AND m.matchingStatus IN (:matchingStatus)
           AND (
                 (:isShouldBeRecruiting = TRUE AND m.recruitment.recruitmentStatus = com.becareful.becarefulserver.domain.matching.domain.RecruitmentStatus.모집중)
                 OR
                 (:isShouldBeRecruiting = FALSE AND m.recruitment.recruitmentStatus <> com.becareful.becarefulserver.domain.matching.domain.RecruitmentStatus.모집중)
               )
    """)
    List<Matching> findAllAppliedByCaregiverAndMatchingStatusIn(
            Caregiver caregiver, List<MatchingStatus> matchingStatus, boolean isShouldBeRecruiting);

    @Modifying
    @Query("DELETE FROM Matching m WHERE m.workApplication.caregiver = :caregiver AND m.matchingStatus <> :status")
    void deleteAllByCaregiverAndStatusNot(
            @Param("caregiver") Caregiver caregiver, @Param("status") MatchingStatus status);

    @Query(
            """
        SELECT count(*) > 0
          FROM Matching m
         WHERE m.recruitment = :recruitment
           AND m.matchingStatus <> '미지원'
    """)
    boolean existsByApplicantOrProcessingContract(Recruitment recruitment);

    void deleteAllByRecruitment(Recruitment recruitment);
}
