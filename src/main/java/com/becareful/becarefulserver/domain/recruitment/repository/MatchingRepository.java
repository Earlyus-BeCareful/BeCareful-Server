package com.becareful.becarefulserver.domain.recruitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.becareful.becarefulserver.domain.recruitment.domain.Matching;

public interface MatchingRepository extends JpaRepository<Matching, Long> {}
