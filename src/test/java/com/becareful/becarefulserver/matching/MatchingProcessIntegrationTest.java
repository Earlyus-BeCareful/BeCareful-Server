package com.becareful.becarefulserver.matching;

import static org.assertj.core.api.Assertions.assertThat;

import com.becareful.becarefulserver.common.IntegrationTest;
import com.becareful.becarefulserver.common.WithCaregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryUnitType;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.caregiver.domain.vo.CaregiverInfo;
import com.becareful.becarefulserver.domain.caregiver.dto.request.WorkApplicationUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationRepository;
import com.becareful.becarefulserver.domain.caregiver.service.WorkApplicationService;
import com.becareful.becarefulserver.domain.chat.dto.request.ContractEditRequest;
import com.becareful.becarefulserver.domain.chat.service.CaregiverChatService;
import com.becareful.becarefulserver.domain.chat.service.SocialWorkerChatService;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.common.domain.vo.Location;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.matching.repository.CompletedMatchingRepository;
import com.becareful.becarefulserver.domain.matching.repository.ContractRepository;
import com.becareful.becarefulserver.domain.matching.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.matching.repository.RecruitmentRepository;
import com.becareful.becarefulserver.domain.matching.service.CaregiverMatchingService;
import com.becareful.becarefulserver.domain.matching.service.SocialWorkerMatchingService;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.fixture.NursingInstitutionFixture;
import java.time.*;
import java.util.EnumSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MatchingProcessIntegrationTest extends IntegrationTest {

    @Autowired
    private CaregiverRepository caregiverRepository;

    @Autowired
    private WorkApplicationService workApplicationService;

    @Autowired
    private WorkApplicationRepository workApplicationRepository;

    @Autowired
    private ElderlyRepository elderlyRepository;

    @Autowired
    private SocialWorkerMatchingService socialWorkerMatchingService;

    @Autowired
    private MatchingRepository matchingRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private SocialWorkerChatService socialWorkerChatService;

    @Autowired
    private CaregiverChatService caregiverChatService;

    @Autowired
    private CompletedMatchingRepository completedMatchingRepository;

    @Autowired
    private CaregiverMatchingService caregiverMatchingService;

    @Test
    @WithCaregiver(phoneNumber = "01099990000")
    void 매칭_과정_전체_플로우() {
        Caregiver caregiver = caregiverRepository.save(Caregiver.create(
                "caregiver",
                LocalDate.of(1990, 1, 1),
                Gender.FEMALE,
                "01099990000",
                null,
                "서울시",
                "상세주소",
                new CaregiverInfo(false, false, null, null, null),
                true));

        WorkApplicationUpdateRequest workRequest = new WorkApplicationUpdateRequest(
                List.of(Location.of("서울시", "종로구", "청운동")),
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                List.of(WorkTime.MORNING),
                List.of(CareType.식사보조),
                WorkSalaryUnitType.DAY,
                10000);
        workApplicationService.updateWorkApplication(workRequest);

        Elderly elderly = elderlyRepository.save(Elderly.create(
                "어르신",
                LocalDate.of(1950, 1, 1),
                Gender.FEMALE,
                Location.of("서울시", "종로구", "청운동"),
                "상세",
                false,
                false,
                null,
                NursingInstitutionFixture.NURSING_INSTITUTION,
                CareLevel.일등급,
                "건강",
                EnumSet.of(DetailCareType.스스로식사가능)));

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

        Recruitment recruitment = recruitmentRepository.findById(recruitmentId).orElseThrow();
        WorkApplication workApp =
                workApplicationRepository.findByCaregiver(caregiver).orElseThrow();
        Matching matching = matchingRepository
                .findByWorkApplicationAndRecruitment(workApp, recruitment)
                .orElseThrow();

        caregiverMatchingService.applyRecruitment(recruitmentId);
        Matching applied = matchingRepository
                .findByIdWithRecruitmentAndWorkApplicationAndCaregiver(matching.getId())
                .orElseThrow();
        assertThat(applied.isApplicationReviewing()).isTrue();

        socialWorkerMatchingService.propose(applied.getId(), LocalDate.now());
        Contract firstContract = contractRepository
                .findTop1ByMatchingOrderByCreateDateDesc(matching)
                .orElseThrow();
        assertThat(firstContract.getWorkSalaryAmount()).isEqualTo(10000);

        ContractEditRequest editRequest = new ContractEditRequest(
                matching.getId(),
                List.of(DayOfWeek.MONDAY),
                LocalTime.of(10, 0),
                LocalTime.of(13, 0),
                WorkSalaryUnitType.DAY,
                12000,
                LocalDate.now(),
                List.of(CareType.식사보조));
        socialWorkerChatService.editContract(editRequest);
        List<Contract> contracts = contractRepository.findByMatchingOrderByCreateDateAsc(matching);
        Contract edited = contracts.get(contracts.size() - 1);
        assertThat(edited.getWorkSalaryAmount()).isEqualTo(12000);

        caregiverChatService.createCompletedMatching(edited.getId());
        assertThat(completedMatchingRepository.existsCompletedMatchingByContract(edited))
                .isTrue();
    }
}
