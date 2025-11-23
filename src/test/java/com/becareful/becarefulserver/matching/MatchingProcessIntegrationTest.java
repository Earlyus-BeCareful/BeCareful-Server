package com.becareful.becarefulserver.matching;

import static org.assertj.core.api.Assertions.*;

import com.becareful.becarefulserver.common.*;
import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.caregiver.dto.request.*;
import com.becareful.becarefulserver.domain.caregiver.repository.*;
import com.becareful.becarefulserver.domain.caregiver.service.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.chat.dto.request.*;
import com.becareful.becarefulserver.domain.chat.repository.*;
import com.becareful.becarefulserver.domain.chat.service.*;
import com.becareful.becarefulserver.domain.common.domain.*;
import com.becareful.becarefulserver.domain.common.domain.vo.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.dto.request.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import com.becareful.becarefulserver.domain.matching.service.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.*;
import com.becareful.becarefulserver.domain.socialworker.repository.*;
import com.becareful.becarefulserver.fixture.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;

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

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Test
    @WithCaregiver(phoneNumber = "01099990000")
    void 매칭_과정_전체_플로우() {
        Caregiver caregiver =
                caregiverRepository.findByPhoneNumber("01099990000").orElseThrow();

        WorkApplicationCreateOrUpdateRequest workRequest = new WorkApplicationCreateOrUpdateRequest(
                List.of(Location.of("서울시", "종로구", "청운동")),
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                List.of(WorkTime.MORNING),
                List.of(CareType.식사보조),
                WorkSalaryUnitType.DAY,
                10000);
        workApplicationService.createOrUpdateWorkApplication(workRequest);

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
        Matching applied = matchingRepository.findById(matching.getId()).orElseThrow();
        assertThat(applied.isApplicationReviewing()).isTrue();

        long chatRoomId =
                socialWorkerMatchingService.proposeMatching(recruitmentId, caregiver.getId(), LocalDate.now());

        Contract firstContract =
                (Contract) chatRepository.findLastChatWithContent(chatRoomId).orElseThrow();
        assertThat(firstContract.getWorkSalaryAmount()).isEqualTo(10000);

        EditContractChatRequest editRequest = new EditContractChatRequest(
                ChatSendRequestType.EDIT_CONTRACT,
                List.of(DayOfWeek.MONDAY),
                LocalTime.of(10, 0),
                LocalTime.of(13, 0),
                WorkSalaryUnitType.DAY,
                12000,
                LocalDate.now(),
                List.of(CareType.식사보조));

        socialWorkerChatService.editContractChat(chatRoomId, editRequest);
        Contract edited = contractRepository
                .findDistinctTopByChatRoomIdOrderByCreateDateDesc(chatRoomId)
                .orElseThrow();

        assertThat(edited.getWorkSalaryAmount()).isEqualTo(12000);

        ConfirmContractChatRequest confirmRequest =
                new ConfirmContractChatRequest(ChatSendRequestType.CONFIRM_MATCHING, edited.getId());

        socialWorkerChatService.confirmContractChat(chatRoomId, confirmRequest);
        assertThat(completedMatchingRepository.existsCompletedMatchingByContract(edited))
                .isTrue();
    }
}
