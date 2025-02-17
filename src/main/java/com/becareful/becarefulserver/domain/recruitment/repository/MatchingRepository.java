package com.becareful.becarefulserver.domain.recruitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.recruitment.domain.Matching;
import com.becareful.becarefulserver.domain.recruitment.domain.Recruitment;

import java.util.List;
import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    @Query("SELECT m.recruitment "
            + "FROM Matching m "
            + "WHERE m.workApplication = :workApplication "
            + "AND m.matchingStatus = '미지원'")
    List<Recruitment> findAllRecruitmentByWorkApplication(WorkApplication workApplication);

    Optional<Matching> findByWorkApplicationAndRecruitment(WorkApplication workApplication, Recruitment recruitment);
}
