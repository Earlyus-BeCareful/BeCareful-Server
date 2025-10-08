package com.becareful.becarefulserver.matching;

import com.becareful.becarefulserver.common.IntegrationTest;
import com.becareful.becarefulserver.common.WithSocialWorker;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import com.becareful.becarefulserver.domain.matching.dto.request.WaitingMatchingElderlySearchRequest;
import com.becareful.becarefulserver.domain.matching.repository.RecruitmentRepository;
import com.becareful.becarefulserver.domain.matching.service.SocialWorkerMatchingService;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.fixture.ElderlyFixture;
import com.becareful.becarefulserver.fixture.RecruitmentFixture;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ElderlySimpleDto> waitingElderlys = socialWorkerMatchingService.getWaitingElderlys(pageable);

        // then
        List<String> elderlyNames = waitingElderlys.getContent().stream()
                .map(ElderlySimpleDto::elderlyName)
                .toList();

        Assertions.assertThat(elderlyNames).containsExactlyInAnyOrder("박요양", "김요양");
    }

    @Test
    @WithSocialWorker(phoneNumber = "01099990000")
    void 매칭_대기중인_어르신을_검색한다() {
        // given
        Elderly elderly1 = createElderly("박요양");
        Elderly elderly2 = createElderly("김요양");
        Elderly elderly3 = createElderly("최요양");

        createRecruitment("모집 공고", elderly3);

        Pageable pageable = PageRequest.of(0, 10);

        WaitingMatchingElderlySearchRequest request = new WaitingMatchingElderlySearchRequest("박");

        // when
        Page<ElderlySimpleDto> waitingElderlys = socialWorkerMatchingService.searchWaitingElderlys(pageable, request);

        // then
        List<String> elderlyNames = waitingElderlys.getContent().stream()
                .map(ElderlySimpleDto::elderlyName)
                .toList();

        Assertions.assertThat(elderlyNames).containsExactly("박요양");
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
