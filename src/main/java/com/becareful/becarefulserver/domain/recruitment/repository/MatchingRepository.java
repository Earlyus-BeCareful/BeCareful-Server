package com.becareful.becarefulserver.domain.recruitment.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.recruitment.domain.Matching;
import com.becareful.becarefulserver.domain.recruitment.domain.MatchingStatus;
import com.becareful.becarefulserver.domain.recruitment.domain.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    @Query("SELECT m "
            + "FROM Matching m "
            + "WHERE m.workApplication = :workApplication "
            + "AND m.matchingStatus = '미지원'")
    List<Matching> findAllByWorkApplication(WorkApplication workApplication);

    List<Matching> findByRecruitment(Recruitment recruitment);
    int countByRecruitmentAndMatchingStatus(Recruitment recruitment, MatchingStatus matchingStatus);

    Optional<Matching> findByWorkApplicationAndRecruitment(WorkApplication workApplication, Recruitment recruitment);

    List<Matching> findByWorkApplicationAndMatchingStatus(WorkApplication workApplication, MatchingStatus matchingStatus);
}
