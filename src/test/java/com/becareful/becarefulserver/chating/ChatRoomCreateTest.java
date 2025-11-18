package com.becareful.becarefulserver.chating;

import static com.becareful.becarefulserver.fixture.NursingInstitutionFixture.NURSING_INSTITUTION;
import static org.assertj.core.api.Assertions.*;

import com.becareful.becarefulserver.common.*;
import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.caregiver.repository.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.repository.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import com.becareful.becarefulserver.domain.matching.service.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.domain.socialworker.repository.*;
import com.becareful.becarefulserver.fixture.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;

public class ChatRoomCreateTest extends IntegrationTest {

    @Autowired
    private CaregiverRepository caregiverRepository;

    @Autowired
    private SocialWorkerRepository socialWorkerRepository;

    @Autowired
    private ElderlyRepository elderlyRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private CaregiverChatReadStatusRepository caregiverChatReadStatusRepository;

    @Autowired
    private SocialWorkerChatReadStatusRepository socialWorkerChatReadStatusRepository;

    @Autowired
    private SocialWorkerMatchingService socialWorkerMatchingService;

    @Autowired
    private MatchingRepository matchingRepository;

    @Autowired
    private WorkApplicationRepository workApplicationRepository;

    @Test
    @WithCaregiver(phoneNumber = "01099990000")
    void 채팅방_생성_및_읽음상태_계약_검증() {
        // 1. Fixture로 데이터 생성
        Caregiver caregiver =
                caregiverRepository.findByPhoneNumber("01099990000").orElseThrow();

        WorkApplication workApplication =
                workApplicationRepository.save(WorkApplicationFixture.createApplicationFixture(caregiver));

        Elderly elderly = elderlyRepository.save(ElderlyFixture.create("김춘자"));

        Recruitment recruitment = recruitmentRepository.save(RecruitmentFixture.createRecruitment("테스트 공고", elderly));

        matchingRepository.save(Matching.create(recruitment, workApplication));

        socialWorkerRepository.save(SocialWorkerFixture.SOCIAL_WORKER_1);
        socialWorkerRepository.save(SocialWorkerFixture.SOCIAL_WORKER_MANAGER);

        // 2. 채팅방 생성 호출
        long chatRoomId =
                socialWorkerMatchingService.proposeMatching(recruitment.getId(), caregiver.getId(), LocalDate.now());

        // 3. ChatRoom 검증
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
        assertThat(chatRoom.getRecruitment()).isEqualTo(recruitment);

        // 4. Caregiver 읽음 상태 검증
        CaregiverChatReadStatus caregiverStatus = caregiverChatReadStatusRepository
                .findByCaregiverAndChatRoom(caregiver, chatRoom)
                .orElseThrow();
        assertThat(caregiverStatus.getChatRoom()).isEqualTo(chatRoom);

        // 5. SocialWorker 읽음 상태 검증
        List<SocialWorker> socialWorkers = socialWorkerRepository.findAllByNursingInstitution(NURSING_INSTITUTION);
        for (SocialWorker sw : socialWorkers) {
            assertThat(socialWorkerChatReadStatusRepository.findBySocialWorkerAndChatRoom(sw, chatRoom))
                    .isPresent();
        }

        // 6. 최초 계약(Contract) 검증
        Contract contract = contractRepository
                .findDistinctTopByChatRoomIdOrderByCreateDateDesc(chatRoomId)
                .orElseThrow();
        assertThat(contract.getChatRoom()).isEqualTo(chatRoom);
        assertThat(contract.getChatRoom().getRecruitment()).isEqualTo(recruitment);
    }
}
