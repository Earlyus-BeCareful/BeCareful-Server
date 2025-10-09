package com.becareful.becarefulserver.domain.matching.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.*;
import jakarta.transaction.*;
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

    @Query(
            """
    DELETE
      FROM Matching m
     WHERE m.workApplication = :application
       AND m.matchingStatus = :matchingStatus
    """)
    void deleteAllByApplicationAndMatchingStatus(WorkApplication application, MatchingStatus matchingStatus);

    List<Matching> findAllByRecruitment(Recruitment recruitment);

    int countByRecruitment(Recruitment recruitment);

    int countByRecruitmentAndMatchingStatus(Recruitment recruitment, MatchingStatus matchingStatus);

    Optional<Matching> findByWorkApplicationAndRecruitment(WorkApplication workApplication, Recruitment recruitment);

    @Query(
            "SELECT m FROM Matching m WHERE m.workApplication.caregiver = :caregiver AND m.recruitment.id = :recruitmentId")
    Optional<Matching> findByCaregiverAndRecruitmentId(Caregiver caregiver, Long recruitmentId);

    List<Matching> findByWorkApplicationAndMatchingStatus(
            WorkApplication workApplication, MatchingStatus matchingStatus);

    @Query("SELECT m FROM Matching m JOIN FETCH m.recruitment WHERE m.id = :id")
    Optional<Matching> findByIdWithRecruitment(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM Matching m WHERE m.workApplication.caregiver = :caregiver AND m.matchingStatus <> :status")
    void deleteAllByCaregiverAndStatusNot(
            @Param("caregiver") Caregiver caregiver, @Param("status") MatchingStatus status);

    @Query(
            """
    SELECT m FROM Matching m
    JOIN FETCH m.recruitment r
    JOIN FETCH m.workApplication w
    JOIN FETCH w.caregiver c
    WHERE m.id = :id
    """)
    Optional<Matching> findByIdWithRecruitmentAndWorkApplicationAndCaregiver(@Param("id") Long id);
}
