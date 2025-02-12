package com.becareful.becarefulserver.domain.caregiver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;

public interface CaregiverRepository extends JpaRepository<Caregiver, Long> {}
