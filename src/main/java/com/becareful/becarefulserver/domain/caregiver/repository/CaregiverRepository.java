package com.becareful.becarefulserver.domain.caregiver.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface CaregiverRepository extends JpaRepository<Caregiver, Long> {

    Optional<Caregiver> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT c.profileImageUrl FROM Caregiver c WHERE c.profileImageUrl IS NOT NULL")
    Set<String> findAllProfileImageUrls();
}
