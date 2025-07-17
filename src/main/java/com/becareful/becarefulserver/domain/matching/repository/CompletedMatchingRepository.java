package com.becareful.becarefulserver.domain.matching.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.matching.domain.CompletedMatching;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompletedMatchingRepository extends JpaRepository<CompletedMatching, Long> {

    @Query(
            "SELECT COUNT(DISTINCT cm.caregiver) FROM CompletedMatching cm WHERE cm.contract.matching.recruitment.elderly = :elderly")
    int countDistinctCaregiversByElderly(@Param("elderly") Elderly elderly);

    boolean existsCompletedMatchingByContract(Contract contract);

    List<CompletedMatching> findByCaregiver(Caregiver caregiver);
}
