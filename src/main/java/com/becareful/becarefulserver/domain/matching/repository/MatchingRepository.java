package com.becareful.becarefulserver.domain.matching.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.MatchingStatus;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.nursingInstitution.domain.NursingInstitution;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    @Query("SELECT m "
            + "FROM Matching m "
            + "WHERE m.workApplication = :workApplication "
            + "AND m.matchingStatus = '미지원'")
    List<Matching> findAllByWorkApplication(WorkApplication workApplication);

    @Query(
            """
    SELECT m FROM Matching m
    JOIN m.recruitment r
    JOIN r.elderly e
    WHERE e.nursingInstitution = :nursingInstitution
    AND m.matchingStatus = '합격'
    """)
    List<Matching> findByNursingInstitution(@Param("nursingInstitution") NursingInstitution nursingInstitution);

    @Query(
            """
    SELECT m FROM Matching m
    JOIN m.workApplication w
    WHERE w.caregiver = :caregiver
    AND m.matchingStatus = '합격'
    """)
    List<Matching> findByCaregiver(@Param("caregiver") Caregiver caregiver);

    List<Matching> findByRecruitmentId(Long recruitmentId);

    int countByRecruitmentIdAndMatchingStatusNot(Long recruitmentId, String matchingStatus);

    int countByRecruitmentIdAndMatchingStatus(Long recruitmentId, String matchingStatus);

    List<Matching> findByRecruitment(Recruitment recruitment);

    int countByRecruitmentAndMatchingStatus(Recruitment recruitment, MatchingStatus matchingStatus);

    Optional<Matching> findByWorkApplicationAndRecruitment(WorkApplication workApplication, Recruitment recruitment);

    List<Matching> findByWorkApplicationAndMatchingStatus(
            WorkApplication workApplication, MatchingStatus matchingStatus);

    @Query("SELECT m " + "FROM Matching m " + "WHERE m.recruitment.elderly.id IN :elderlyIds ")
    List<Matching> findAllMatchingByElderlyIds(@Param("elderlyIds") List<Long> elderlyIds);
}
