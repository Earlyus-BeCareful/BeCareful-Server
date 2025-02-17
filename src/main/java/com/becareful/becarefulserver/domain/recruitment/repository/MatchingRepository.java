package com.becareful.becarefulserver.domain.recruitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.recruitment.domain.Matching;
import com.becareful.becarefulserver.domain.recruitment.domain.Recruitment;

import java.util.List;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    @Query("SELECT m.recruitment "
            + "FROM Matching m "
            + "WHERE m.workApplication = :workApplication")
    List<Recruitment> findAllRecruitmentByWorkApplication(WorkApplication workApplication);
}
