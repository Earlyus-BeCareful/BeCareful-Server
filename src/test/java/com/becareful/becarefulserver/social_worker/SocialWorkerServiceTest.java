package com.becareful.becarefulserver.social_worker;

import com.becareful.becarefulserver.common.IntegrationTest;
import com.becareful.becarefulserver.common.WithSocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.domain.socialworker.service.SocialWorkerService;
import com.becareful.becarefulserver.global.exception.ErrorMessage;
import com.becareful.becarefulserver.global.exception.exception.SocialWorkerException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SocialWorkerServiceTest extends IntegrationTest {

    @Autowired
    private SocialWorkerService socialWorkerService;

    @Autowired
    private SocialWorkerRepository socialWorkerRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @WithSocialWorker(phoneNumber = "01012345678")
    void 협회에_가입한_상태라면_회원_탈퇴에_실패한다() {
        // given
        // 010 1234 5678 사회복지사는 협회에 가입한 사회복지사, IntegrationTest 참고

        // when & then
        Assertions.assertThatThrownBy(() -> socialWorkerService.deleteSocialWorker())
                .isInstanceOf(SocialWorkerException.class)
                .hasMessage(ErrorMessage.SOCIAL_WORKER_NOT_DELETABLE_HAS_ASSOCIATION);
    }

    @Test
    @WithSocialWorker(phoneNumber = "01099990000")
    void 협회에_가입하지_않았다면_회원_탈퇴에_성공한다() {
        // given
        // 010 9999 0000 사회복지사는 협회에 가입하지 않은 사회복지사, IntegrationTest 참고
        Long id = socialWorkerRepository
                .findByPhoneNumber("01099990000")
                .orElseThrow()
                .getId();

        // when
        socialWorkerService.deleteSocialWorker();

        // then
        em.flush();
        em.clear();

        List<SocialWorker> socialWorkers = em.createNativeQuery(
                        """
                            SELECT *
                              FROM social_worker s
                             WHERE s.social_worker_id = ?
                         """,
                        SocialWorker.class)
                .setParameter(1, id)
                .getResultList();

        Assertions.assertThat(socialWorkers).hasSize(1);
        SocialWorker socialWorker = socialWorkers.get(0);

        Assertions.assertThat(socialWorker.isDeleted()).isTrue();
        Assertions.assertThat(socialWorker.getDeleteDate()).isNotNull();
        Assertions.assertThat(socialWorker.getName()).isNull();
        Assertions.assertThat(socialWorker.getNickname()).isNull();
        Assertions.assertThat(socialWorker.getBirthday()).isNull();
        Assertions.assertThat(socialWorker.getGender()).isNull();
        Assertions.assertThat(socialWorker.getPhoneNumber()).isNull();
        Assertions.assertThat(socialWorker.getId()).isNotNull();
    }
}
