package com.becareful.becarefulserver.domain.matching.repository;

import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByMatchingIdOrderByCreateDateAsc(Long matchingId);

    @Query(
            """
    SELECT c FROM Contract c
    JOIN c.matching m
    WHERE m = :matching
    ORDER BY c.createDate DESC
""")
    List<Contract> findLatestContractByMatching(@Param("matching") Matching matching);
}
