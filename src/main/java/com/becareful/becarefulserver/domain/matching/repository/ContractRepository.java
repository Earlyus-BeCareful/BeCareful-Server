package com.becareful.becarefulserver.domain.matching.repository;

import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByMatchingIdOrderByCreateDateAsc(Long matchingId);

    Optional<Contract> findTop1ByMatchingOrderByCreateDateDesc(Matching matching);
}
