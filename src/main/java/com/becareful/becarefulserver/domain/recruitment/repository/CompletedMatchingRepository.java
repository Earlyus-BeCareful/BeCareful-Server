package com.becareful.becarefulserver.domain.recruitment.repository;

import com.becareful.becarefulserver.domain.recruitment.domain.CompletedMatching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompletedMatchingRepository extends JpaRepository<CompletedMatching, Long> {
    List<CompletedMatching> findByCaregiver_Id(Long caregiverId);
}
