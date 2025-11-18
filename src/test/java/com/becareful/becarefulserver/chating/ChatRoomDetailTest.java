package com.becareful.becarefulserver.chating;

import com.becareful.becarefulserver.common.IntegrationTest;
import com.becareful.becarefulserver.common.WithCaregiver;
import com.becareful.becarefulserver.common.WithSocialWorker;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryUnitType;
import com.becareful.becarefulserver.domain.caregiver.domain.vo.CaregiverInfo;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationRepository;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;
import com.becareful.becarefulserver.domain.chat.repository.CaregiverChatReadStatusRepository;
import com.becareful.becarefulserver.domain.chat.repository.ChatRepository;
import com.becareful.becarefulserver.domain.chat.repository.ChatRoomRepository;
import com.becareful.becarefulserver.domain.chat.repository.SocialWorkerChatReadStatusRepository;
import com.becareful.becarefulserver.domain.chat.service.CaregiverChatService;
import com.becareful.becarefulserver.domain.chat.service.SocialWorkerChatService;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.common.domain.vo.Location;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.matching.repository.ContractRepository;
import com.becareful.becarefulserver.domain.matching.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.matching.repository.RecruitmentRepository;
import com.becareful.becarefulserver.domain.matching.service.SocialWorkerMatchingService;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.fixture.NursingInstitutionFixture;
import com.becareful.becarefulserver.fixture.SocialWorkerFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;

import static com.becareful.becarefulserver.fixture.NursingInstitutionFixture.NURSING_INSTITUTION;
import static org.assertj.core.api.Assertions.assertThat;

public class ChatRoomDetailTest extends IntegrationTest {
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
    private CaregiverChatReadStatusRepository caregiverChatReadStatusRepository;

    @Autowired
    private SocialWorkerChatReadStatusRepository socialWorkerChatReadStatusRepository;

    @Autowired
    private SocialWorkerChatService socialWorkerChatService;

    @Autowired
    private CaregiverChatService caregiverChatService;
    @Autowired
    private ChatRepository chatRepository;

    @Test
    @WithSocialWorker(phoneNumber = "01099990000")
    void 채팅방_메시지전송_검증() {
        // 1. Fixture로 데이터 생성
        Caregiver caregiver = caregiverRepository.save(createCaregiver("01011112222"));
        SocialWorker socialWorker = socialWorkerRepository.save(SocialWorkerFixture.SOCIAL_WORKER_1);
        Elderly elderly = elderlyRepository.save(createElderly(NURSING_INSTITUTION));
        Recruitment recruitment = recruitmentRepository.save(createRecruitment("테스트 공고", elderly));

        ChatRoom chatRoom = chatRoomRepository.save(createChatRoom(recruitment));

        socialWorkerChatReadStatusRepository.save(createSocialWorkerChatReadStatus(socialWorker, chatRoom));
        caregiverChatReadStatusRepository.save(createCaregiverChatReadStatus(caregiver, chatRoom));

        assertThat(socialWorkerChatService.checkNewChat()).isEqualTo(true);
        assertThat(caregiverChatService.checkNewChat()).isEqualTo(true);

        caregiverChatService.getChatRoomDetail(chatRoom.getId());
        assertThat(caregiverChatService.checkNewChat()).isEqualTo(false);

        chatRepository.save(createTextChat(chatRoom));
        assertThat(caregiverChatService.checkNewChat()).isEqualTo(true);
    }

    public static Caregiver createCaregiver(String phoneNumber) {
        return Caregiver.create(
                "caregiver",
                LocalDate.of(1990, 1, 1),
                Gender.FEMALE,
                phoneNumber,
                null,
                "서울시",
                "상세주소",
                new CaregiverInfo(false, false, null, null, null),
                true);
    }

    public static Elderly createElderly(NursingInstitution institution) {
        return Elderly.create(
                "어르신",
                LocalDate.of(1950, 1, 1),
                Gender.FEMALE,
                Location.of("서울시", "종로구", "청운동"),
                "상세주소",
                false,
                false,
                null,
                institution,
                CareLevel.일등급,
                "건강",
                EnumSet.of(DetailCareType.스스로식사가능));
    }

    public static Recruitment createRecruitment(String title, Elderly elderly) {
        RecruitmentCreateRequest request = new RecruitmentCreateRequest(
                elderly.getId(),
                title,
                List.of(DayOfWeek.MONDAY),
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                List.of(CareType.식사보조),
                WorkSalaryUnitType.DAY,
                10000,
                "description");
        return Recruitment.create(request, elderly);
    }

    public static ChatRoom createChatRoom(Recruitment recruitment) {
        return ChatRoom.create(recruitment);
    }

    public static CaregiverChatReadStatus createCaregiverChatReadStatus(Caregiver caregiver, ChatRoom chatRoom) {
        return CaregiverChatReadStatus.create(caregiver, chatRoom);
    }

    public static SocialWorkerChatReadStatus createSocialWorkerChatReadStatus(SocialWorker socialWorker, ChatRoom chatRoom) {
        return SocialWorkerChatReadStatus.create(socialWorker, chatRoom);
    }

    public static Chat createTextChat(ChatRoom chatRoom) {
        return TextChat.create(chatRoom,ChatSenderType.SOCIAL_WORKER, "권찬 최고");
    }
}
