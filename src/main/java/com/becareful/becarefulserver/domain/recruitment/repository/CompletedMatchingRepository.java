package com.becareful.becarefulserver.domain.recruitment.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.recruitment.domain.CompletedMatching;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompletedMatchingRepository extends JpaRepository<CompletedMatching, Long> {

    @Query("SELECT COUNT(DISTINCT cm.caregiver) FROM CompletedMatching cm WHERE cm.elderly = :elderly")
    int countDistinctCaregiversByElderly(@Param("elderly") Elderly elderly);

    List<CompletedMatching> findByCaregiver(Caregiver caregiver);
}
