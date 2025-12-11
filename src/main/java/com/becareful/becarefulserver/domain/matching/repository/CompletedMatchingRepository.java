package com.becareful.becarefulserver.domain.matching.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.chat.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.CompletedMatching;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CompletedMatchingRepository extends JpaRepository<CompletedMatching, Long> {

    boolean existsCompletedMatchingByContract(Contract contract);

    List<CompletedMatching> findByCaregiver(Caregiver caregiver);

    @Query(
            """
        SELECT cm
          FROM CompletedMatching cm
         WHERE cm.contract.chatRoom.recruitment.elderly = :elderly
    """)
    List<CompletedMatching> findAllByElderly(Elderly elderly);

    void deleteByCaregiver(Caregiver caregiver);
}
