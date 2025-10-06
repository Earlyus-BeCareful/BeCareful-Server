package com.becareful.becarefulserver.matching;

import com.becareful.becarefulserver.common.IntegrationTest;
import com.becareful.becarefulserver.common.WithSocialWorker;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import com.becareful.becarefulserver.domain.matching.repository.RecruitmentRepository;
import com.becareful.becarefulserver.domain.matching.service.SocialWorkerMatchingService;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.fixture.ElderlyFixture;
import com.becareful.becarefulserver.fixture.RecruitmentFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SocialWorkerMatchingTest extends IntegrationTest {

    @Autowired
    private ElderlyRepository elderlyRepository;

    @Autowired
    private SocialWorkerMatchingService socialWorkerMatchingService;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Test
    @WithSocialWorker(phoneNumber = "01099990000")
    void 매칭_대기중인_어르신을_조회한다() {
        // given
        Elderly elderly1 = createElderly("박요양");
        Elderly elderly2 = createElderly("김요양");
        Elderly elderly3 = createElderly("최요양");

        createRecruitment("모집 공고", elderly3);

        // when
        List<ElderlySimpleDto> waitingElderlys = socialWorkerMatchingService.getWaitingElderlys();

        // then
        Assertions.assertThat(waitingElderlys.size()).isEqualTo(2);
        Assertions.assertThat(waitingElderlys.get(0).elderlyName()).isIn("박요양", "김요양");
        Assertions.assertThat(waitingElderlys.get(1).elderlyName()).isIn("박요양", "김요양");
    }

    private Elderly createElderly(String name) {
        Elderly elderly = ElderlyFixture.create(name);
        return elderlyRepository.save(elderly);
    }

    private Recruitment createRecruitment(String title, Elderly elderly) {
        Recruitment recruitment = RecruitmentFixture.createRecruitment(title, elderly);
        return recruitmentRepository.save(recruitment);
    }
}
