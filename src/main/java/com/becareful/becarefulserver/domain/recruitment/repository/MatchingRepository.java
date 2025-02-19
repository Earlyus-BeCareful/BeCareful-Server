package com.becareful.becarefulserver.domain.recruitment.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.recruitment.domain.Matching;
import com.becareful.becarefulserver.domain.recruitment.domain.MatchingStatus;
import com.becareful.becarefulserver.domain.recruitment.domain.Recruitment;
import com.becareful.becarefulserver.domain.socialworker.domain.NursingInstitution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    @Query("SELECT m "
            + "FROM Matching m "
            + "WHERE m.workApplication = :workApplication "
            + "AND m.matchingStatus = '미지원'")
    List<Matching> findAllByWorkApplication(WorkApplication workApplication);


    @Query("""
    SELECT m FROM Matching m
    JOIN m.recruitment r
    JOIN r.elderly e
    WHERE e.nursingInstitution = :nursingInstitution
""")
    List<Matching> findByNursingInstitution(@Param("nursingInstitution") NursingInstitution nursingInstitution);

    List<Matching> findByRecruitmentId(Long recruitmentId);
    int countByRecruitmentIdAndMatchingStatusNot(Long recruitmentId, String matchingStatus);
    int countByRecruitmentIdAndMatchingStatus(Long recruitmentId, String matchingStatus);

    List<Matching> findByRecruitment(Recruitment recruitment);
    int countByRecruitmentAndMatchingStatus(Recruitment recruitment, MatchingStatus matchingStatus);


    Optional<Matching> findByWorkApplicationAndRecruitment(WorkApplication workApplication, Recruitment recruitment);

    List<Matching> findByWorkApplicationAndMatchingStatus(WorkApplication workApplication, MatchingStatus matchingStatus);
}
