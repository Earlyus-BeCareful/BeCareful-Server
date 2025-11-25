package com.becareful.becarefulserver.caregiver;

import com.becareful.becarefulserver.common.IntegrationTest;
import com.becareful.becarefulserver.common.WithCaregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.caregiver.service.CaregiverService;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CaregiverServiceTest extends IntegrationTest {

    @Autowired
    private CaregiverService caregiverService;

    @Autowired
    private CaregiverRepository caregiverRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @WithCaregiver(phoneNumber = "01099990000")
    void 요양보호사_회원탈퇴에_성공한다() {
        // given
        // 010 9999 0000 요양보호사는 Integration Test 에서 세팅
        Long id = caregiverRepository.findByPhoneNumber("01099990000")
                .orElseThrow().getId();

        // when
        caregiverService.deleteCaregiver();

        // then
        em.flush();
        em.clear();

        List<Caregiver> caregivers = em.createNativeQuery(
                        """
                            SELECT *
                              FROM caregiver c
                             WHERE c.caregiver_id = ?
                         """,
                        Caregiver.class)
                .setParameter(1, id)
                .getResultList();

        Assertions.assertThat(caregivers).hasSize(1);
        Caregiver caregiver = caregivers.get(0);

        Assertions.assertThat(caregiver.isDeleted()).isTrue();
        Assertions.assertThat(caregiver.getDeleteDate()).isNotNull();
        Assertions.assertThat(caregiver.getName()).isNull();
        Assertions.assertThat(caregiver.getBirthDate()).isNull();
        Assertions.assertThat(caregiver.getGender()).isNull();
        Assertions.assertThat(caregiver.getPhoneNumber()).isNull();
        Assertions.assertThat(caregiver.getAddress()).isNull();
        Assertions.assertThat(caregiver.getProfileImageUrl()).isNull();
        Assertions.assertThat(caregiver.getId()).isNotNull();
    }
}
