package com.becareful.becarefulserver.matching;

import com.becareful.becarefulserver.common.IntegrationTest;
import com.becareful.becarefulserver.common.WithCaregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryUnitType;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.caregiver.dto.request.WorkApplicationCreateOrUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.caregiver.service.WorkApplicationService;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.common.domain.vo.Location;
import com.becareful.becarefulserver.domain.matching.dto.request.CaregiverAppliedStatusFilter;
import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.matching.service.CaregiverMatchingService;
import com.becareful.becarefulserver.domain.matching.service.SocialWorkerMatchingService;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.fixture.NursingInstitutionFixture;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CaregiverMatchingTest extends IntegrationTest {

    @Autowired
    private CaregiverMatchingService caregiverMatchingService;

    @Autowired
    private WorkApplicationService workApplicationService;

    @Autowired
    private ElderlyRepository elderlyRepository;

    @Autowired
    private SocialWorkerMatchingService socialWorkerMatchingService;

    @Autowired
    private CaregiverRepository caregiverRepository;

    private RecruitmentCreateRequest matchedRecruitmentCreateRequest;
    private RecruitmentCreateRequest unmatchedRecruitmentCreateRequest;

    @BeforeEach
    void setCaregiver() {
        WorkApplicationCreateOrUpdateRequest workRequest = new WorkApplicationCreateOrUpdateRequest(
                List.of(Location.of("서울시", "마포구", "상수동")),
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                List.of(WorkTime.MORNING),
                List.of(CareType.식사보조),
                WorkSalaryUnitType.DAY,
                10000);
        workApplicationService.createOrUpdateWorkApplication(workRequest);

        Elderly elderly = createElderly("어르신", Location.of("서울시", "마포구", "상수동"));
        Long elderlyId = elderly.getId();
        matchedRecruitmentCreateRequest = new RecruitmentCreateRequest(
                elderlyId,
                "title",
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                List.of(CareType.식사보조),
                WorkSalaryUnitType.DAY,
                10000,
                "desc");

        Elderly elderly2 = createElderly("어르신2", Location.of("경기", "수원시 장안구", "율전동"));
        Long elderlyId2 = elderly2.getId();
        unmatchedRecruitmentCreateRequest = new RecruitmentCreateRequest(
                elderlyId2,
                "title",
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                List.of(CareType.식사보조),
                WorkSalaryUnitType.DAY,
                10000,
                "desc");
    }

    @Nested
    class 지원현황_검토중_조회시 {

        @Test
        @WithCaregiver(phoneNumber = "01099990000")
        void 지원한_공고가_조회된다() {
            // given
            Long recruitmentId1 = socialWorkerMatchingService.createRecruitment(matchedRecruitmentCreateRequest);
            Long recruitmentId2 = socialWorkerMatchingService.createRecruitment(matchedRecruitmentCreateRequest);

            caregiverMatchingService.applyRecruitment(recruitmentId1);

            // when
            var response = caregiverMatchingService
                    .getMyAppliedRecruitment(CaregiverAppliedStatusFilter.검토중)
                    .recruitments();

            // then
            Assertions.assertThat(response).hasSize(1);
            Assertions.assertThat(response.get(0).recruitmentInfo().recruitmentId())
                    .isEqualTo(recruitmentId1);
        }

        @Test
        @WithCaregiver(phoneNumber = "01099990000")
        void 지원_후_근무제안이_온_공고가_조회된다() {
            // given
            Caregiver caregiver =
                    caregiverRepository.findByPhoneNumber("01099990000").orElseThrow();
            Long recruitmentId1 = socialWorkerMatchingService.createRecruitment(matchedRecruitmentCreateRequest);
            Long recruitmentId2 = socialWorkerMatchingService.createRecruitment(matchedRecruitmentCreateRequest);

            caregiverMatchingService.applyRecruitment(recruitmentId1);
            socialWorkerMatchingService.propose(recruitmentId1, caregiver.getId(), LocalDate.of(2025, 11, 14));
            socialWorkerMatchingService.propose(recruitmentId2, caregiver.getId(), LocalDate.of(2025, 11, 14));

            // when
            var response = caregiverMatchingService
                    .getMyAppliedRecruitment(CaregiverAppliedStatusFilter.검토중)
                    .recruitments();

            // then
            Assertions.assertThat(response).hasSize(1);
            Assertions.assertThat(response.get(0).recruitmentInfo().recruitmentId())
                    .isEqualTo(recruitmentId1);
        }

        @Test
        @WithCaregiver(phoneNumber = "01099990000")
        void 지원한_공고가_마감됐다면_조회되지_않는다() {
            // given
            Long recruitmentId1 = socialWorkerMatchingService.createRecruitment(matchedRecruitmentCreateRequest);
            Long recruitmentId2 = socialWorkerMatchingService.createRecruitment(matchedRecruitmentCreateRequest);

            caregiverMatchingService.applyRecruitment(recruitmentId1);
            socialWorkerMatchingService.closeRecruitment(recruitmentId1);

            // when
            var response = caregiverMatchingService
                    .getMyAppliedRecruitment(CaregiverAppliedStatusFilter.검토중)
                    .recruitments();

            // then
            Assertions.assertThat(response).hasSize(0);
        }

        @Test
        @WithCaregiver(phoneNumber = "01099990000")
        void 지원_후_근무제안이_온_공고가_마감됐다면_조회되지_않는다() {
            // given
            Caregiver caregiver =
                    caregiverRepository.findByPhoneNumber("01099990000").orElseThrow();
            Long recruitmentId1 = socialWorkerMatchingService.createRecruitment(matchedRecruitmentCreateRequest);
            Long recruitmentId2 = socialWorkerMatchingService.createRecruitment(matchedRecruitmentCreateRequest);

            caregiverMatchingService.applyRecruitment(recruitmentId1);
            socialWorkerMatchingService.propose(recruitmentId1, caregiver.getId(), LocalDate.of(2025, 11, 14));
            socialWorkerMatchingService.propose(recruitmentId2, caregiver.getId(), LocalDate.of(2025, 11, 14));
            socialWorkerMatchingService.closeRecruitment(recruitmentId1);

            // when
            var response = caregiverMatchingService
                    .getMyAppliedRecruitment(CaregiverAppliedStatusFilter.검토중)
                    .recruitments();

            // then
            Assertions.assertThat(response).hasSize(0);
        }
    }

    @Nested
    class 지원현황_마감_조회시 {

        @Test
        @WithCaregiver(phoneNumber = "01099990000")
        void 지원한_공고가_마감됐다면_조회된다() {
            // given
            Long recruitmentId1 = socialWorkerMatchingService.createRecruitment(matchedRecruitmentCreateRequest);
            Long recruitmentId2 = socialWorkerMatchingService.createRecruitment(matchedRecruitmentCreateRequest);

            caregiverMatchingService.applyRecruitment(recruitmentId1);
            socialWorkerMatchingService.closeRecruitment(recruitmentId1);

            // when
            var response = caregiverMatchingService
                    .getMyAppliedRecruitment(CaregiverAppliedStatusFilter.마감)
                    .recruitments();

            // then
            Assertions.assertThat(response).hasSize(1);
            Assertions.assertThat(response.get(0).recruitmentInfo().recruitmentId())
                    .isEqualTo(recruitmentId1);
        }

        @Test
        @WithCaregiver(phoneNumber = "01099990000")
        void 지원_후_근무제안이_온_공고가_마감됐다면_조회된다() {
            // given
            Caregiver caregiver =
                    caregiverRepository.findByPhoneNumber("01099990000").orElseThrow();
            Long recruitmentId1 = socialWorkerMatchingService.createRecruitment(matchedRecruitmentCreateRequest);
            Long recruitmentId2 = socialWorkerMatchingService.createRecruitment(matchedRecruitmentCreateRequest);

            caregiverMatchingService.applyRecruitment(recruitmentId1);
            socialWorkerMatchingService.propose(recruitmentId1, caregiver.getId(), LocalDate.of(2025, 11, 14));
            socialWorkerMatchingService.propose(recruitmentId2, caregiver.getId(), LocalDate.of(2025, 11, 14));
            socialWorkerMatchingService.closeRecruitment(recruitmentId1);

            // when
            var response = caregiverMatchingService
                    .getMyAppliedRecruitment(CaregiverAppliedStatusFilter.마감)
                    .recruitments();

            // then
            Assertions.assertThat(response).hasSize(1);
            Assertions.assertThat(response.get(0).recruitmentInfo().recruitmentId())
                    .isEqualTo(recruitmentId1);
        }
    }

    @Nested
    class 매칭_로직_테스트 {

        @Test
        @WithCaregiver(phoneNumber = "01099990000")
        void 지원서_수정시_매칭_데이터가_수정된다() {
            // given
            Long recruitmentId1 = socialWorkerMatchingService.createRecruitment(matchedRecruitmentCreateRequest);
            Long recruitmentId2 = socialWorkerMatchingService.createRecruitment(unmatchedRecruitmentCreateRequest);

            var prevResponse = caregiverMatchingService.getCaregiverMatchingRecruitmentList();
            Assertions.assertThat(prevResponse).hasSize(1);
            Assertions.assertThat(prevResponse.get(0).recruitmentInfo().recruitmentId()).isEqualTo(recruitmentId1);

            WorkApplicationCreateOrUpdateRequest workRequest = new WorkApplicationCreateOrUpdateRequest(
                    List.of(Location.of("경기", "수원시 장안구", "율전동")),
                    List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                    List.of(WorkTime.MORNING),
                    List.of(CareType.식사보조),
                    WorkSalaryUnitType.DAY,
                    10000);

            // when
            workApplicationService.createOrUpdateWorkApplication(workRequest);

            // then
            var response = caregiverMatchingService.getCaregiverMatchingRecruitmentList();
            Assertions.assertThat(response).hasSize(1);
            Assertions.assertThat(response.get(0).recruitmentInfo().recruitmentId()).isEqualTo(recruitmentId2);
            Assertions.assertThat(response.get(0).recruitmentInfo().workLocation()).isEqualTo(Location.of("경기", "수원시 장안구", "율전동").getShortLocation());
        }
    }

    private Elderly createElderly(String name, Location location) {
        return elderlyRepository.save(Elderly.create(
                name,
                LocalDate.of(1950, 1, 1),
                Gender.FEMALE,
                location,
                "상세",
                false,
                false,
                null,
                NursingInstitutionFixture.NURSING_INSTITUTION,
                CareLevel.일등급,
                "건강",
                EnumSet.of(DetailCareType.스스로식사가능)));
    }
}
