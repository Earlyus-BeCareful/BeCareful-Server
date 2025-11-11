package com.becareful.becarefulserver.matching;

import com.becareful.becarefulserver.common.IntegrationTest;
import com.becareful.becarefulserver.common.WithSocialWorker;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryUnitType;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.caregiver.domain.vo.CaregiverInfo;
import com.becareful.becarefulserver.domain.caregiver.dto.request.WorkApplicationCreateOrUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.caregiver.service.WorkApplicationService;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.common.domain.vo.Location;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.matching.dto.request.WaitingMatchingElderlySearchRequest;
import com.becareful.becarefulserver.domain.matching.repository.RecruitmentRepository;
import com.becareful.becarefulserver.domain.matching.service.CaregiverMatchingService;
import com.becareful.becarefulserver.domain.matching.service.SocialWorkerMatchingService;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.fixture.ElderlyFixture;
import com.becareful.becarefulserver.fixture.RecruitmentFixture;
import com.becareful.becarefulserver.global.exception.ErrorMessage;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private CaregiverRepository caregiverRepository;

    @Autowired
    private WorkApplicationService workApplicationService;

    @Autowired
    private CaregiverMatchingService caregiverMatchingService;

    @BeforeEach
    void setCaregiver() {
        Caregiver caregiver = caregiverRepository.save(Caregiver.create(
                "caregiver",
                LocalDate.of(1990, 1, 1),
                Gender.FEMALE,
                "01099990000",
                null,
                "서울특별시",
                "상세주소",
                new CaregiverInfo(false, false, null, null, null),
                true));

        WorkApplicationCreateOrUpdateRequest workRequest = new WorkApplicationCreateOrUpdateRequest(
                List.of(Location.of("서울특별시", "마포구", "상수동")),
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                List.of(WorkTime.MORNING),
                List.of(CareType.식사보조),
                WorkSalaryUnitType.DAY,
                10000);
        workApplicationService.createOrUpdateWorkApplication(workRequest);
    }

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

    @Test
    @WithSocialWorker(phoneNumber = "01099990000")
    void 공고_삭제에_성공한다() {
        // given
        Elderly elderly = createElderly("박요양");
        RecruitmentCreateRequest recruitmentRequest = new RecruitmentCreateRequest(
                elderly.getId(),
                "title",
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                List.of(CareType.식사보조),
                WorkSalaryUnitType.DAY,
                10000,
                "desc");
        Long recruitmentId = socialWorkerMatchingService.createRecruitment(recruitmentRequest);

        // when
        socialWorkerMatchingService.deleteRecruitment(recruitmentId);

        // then
        Assertions.assertThat(recruitmentRepository.existsById(recruitmentId)).isFalse();
    }

    @Test
    @WithSocialWorker(phoneNumber = "01099990000")
    void 지원자가_있다면_공고_삭제에_실패한다() {
        // given
        Elderly elderly = createElderly("박요양");
        RecruitmentCreateRequest recruitmentRequest = new RecruitmentCreateRequest(
                elderly.getId(),
                "title",
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                List.of(CareType.식사보조),
                WorkSalaryUnitType.DAY,
                10000,
                "desc");
        Long recruitmentId = socialWorkerMatchingService.createRecruitment(recruitmentRequest);

        caregiverMatchingService.applyRecruitment(recruitmentId);

        // when & then
        Assertions.assertThatThrownBy(() -> {
                    socialWorkerMatchingService.deleteRecruitment(recruitmentId);
                })
                .hasMessage(ErrorMessage.RECRUITMENT_NOT_DELETABLE_APPLICANTS_OR_PROCESSING_CONTRACT_EXISTS);
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
