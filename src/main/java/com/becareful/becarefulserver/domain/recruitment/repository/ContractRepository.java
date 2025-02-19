package com.becareful.becarefulserver.domain.recruitment.repository;

import com.becareful.becarefulserver.domain.recruitment.domain.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByMatchingIdOrderByCreateDateAsc(Long matchingId);
}
