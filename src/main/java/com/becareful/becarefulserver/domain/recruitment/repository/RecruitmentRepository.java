package com.becareful.becarefulserver.domain.recruitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.becareful.becarefulserver.domain.recruitment.domain.Recruitment;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {}
