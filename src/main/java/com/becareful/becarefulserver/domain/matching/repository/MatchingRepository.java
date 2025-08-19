package com.becareful.becarefulserver.domain.matching.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    @Query(
            """
    SELECT m FROM Matching m
    JOIN m.recruitment r
    JOIN r.elderly e
    WHERE e.nursingInstitution = :nursingInstitution
    AND m.matchingApplicationStatus = '합격'
    """)
    List<Matching> findAllByNursingInstitution(@Param("nursingInstitution") NursingInstitution nursingInstitution);

    @Query(
            """
    SELECT m FROM Matching m
    JOIN m.workApplication w
    WHERE w.caregiver = :caregiver
    AND m.matchingApplicationStatus = :applicationStatus
    """)
    List<Matching> findAllByCaregiverAndApplicationStatus(
            Caregiver caregiver, MatchingApplicationStatus applicationStatus);

    List<Matching> findAllByRecruitment(Recruitment recruitment);

    int countByRecruitmentAndMatchingApplicationStatus(
            Recruitment recruitment, MatchingApplicationStatus matchingApplicationStatus);

    Optional<Matching> findByWorkApplicationAndRecruitment(WorkApplication workApplication, Recruitment recruitment);

    @Query(
            "SELECT m FROM Matching m WHERE m.workApplication.caregiver = :caregiver AND m.recruitment.id = :recruitmentId")
    Optional<Matching> findByCaregiverAndRecruitmentId(Caregiver caregiver, Long recruitmentId);

    List<Matching> findByWorkApplicationAndMatchingApplicationStatus(
            WorkApplication workApplication, MatchingApplicationStatus matchingApplicationStatus);

    @Query("SELECT m FROM Matching m WHERE m.recruitment.elderly.id IN :elderlyIds ")
    List<Matching> findAllByElderlyIds(@Param("elderlyIds") List<Long> elderlyIds);
}
