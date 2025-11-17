package com.becareful.becarefulserver.Chating;

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
    private WorkApplicationRepository workApplicationRepository;

    @Autowired
    private MatchingRepository matchingRepository;

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

    @Test
    @WithCaregiver(phoneNumber = "01099990000")
    void 채팅방_생성_및_읽음상태_계약_검증() {
        // 1. Fixture로 데이터 생성
        Caregiver caregiver = caregiverRepository.save(MatchingFixture.createCaregiver("01011112222"));
        Elderly elderly =
                elderlyRepository.save(MatchingFixture.createElderly(NursingInstitutionFixture.NURSING_INSTITUTION));
        Recruitment recruitment = recruitmentRepository.save(MatchingFixture.createRecruitment("테스트 공고", elderly));
        WorkApplication workApplication =
                workApplicationRepository.save(MatchingFixture.createWorkApplication(caregiver));
        Matching matching = matchingRepository.save(MatchingFixture.createMatching(recruitment, workApplication));

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
        List<SocialWorker> socialWorkers =
                socialWorkerRepository.findAllByNursingInstitution(NursingInstitutionFixture.NURSING_INSTITUTION);
        for (SocialWorker sw : socialWorkers) {
            assertThat(socialWorkerChatReadStatusRepository.findBySocialWorkerAndChatRoom(sw, chatRoom))
                    .isPresent();
        }

        // 6. 최초 계약(Contract) 검증
        Contract contract = contractRepository.findTopByChatRoomId(chatRoomId).orElseThrow();
        assertThat(contract.getChatRoom()).isEqualTo(chatRoom);
        assertThat(contract.getChatRoom().getRecruitment()).isEqualTo(recruitment);
    }
}
