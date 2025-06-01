package com.becareful.becarefulserver.domain.caregiver.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaregiverRepository extends JpaRepository<Caregiver, Long> {

    Optional<Caregiver> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);
}
