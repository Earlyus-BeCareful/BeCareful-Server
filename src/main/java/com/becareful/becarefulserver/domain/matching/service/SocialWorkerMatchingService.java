package com.becareful.becarefulserver.domain.matching.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.caregiver.repository.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.chat.dto.response.ChatRoomActiveStatusUpdatedChatResponse;
import com.becareful.becarefulserver.domain.chat.repository.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.service.MatchingDomainService;
import com.becareful.becarefulserver.domain.matching.domain.service.RecruitmentDomainService;
import com.becareful.becarefulserver.domain.matching.domain.vo.*;
import com.becareful.becarefulserver.domain.matching.dto.*;
import com.becareful.becarefulserver.domain.matching.dto.request.*;
import com.becareful.becarefulserver.domain.matching.dto.response.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.service.ElderlyDomainService;
import com.becareful.becarefulserver.domain.socialworker.repository.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import com.becareful.becarefulserver.global.util.*;
import jakarta.validation.Valid;
import java.time.*;
import java.util.*;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialWorkerMatchingService {

    private final AuthUtil authUtil;
    private final ElderlyDomainService elderlyDomainService;
    private final MatchingDomainService matchingDomainService;
    private final RecruitmentRepository recruitmentRepository;
    private final ElderlyRepository elderlyRepository;
    private final WorkApplicationRepository workApplicationRepository;
    private final CareerRepository careerRepository;
    private final CaregiverRepository caregiverRepository;
    private final CareerDetailRepository careerDetailRepository;
    private final SocialWorkerRepository socialWorkerRepository;
    private final SocialWorkerChatReadStatusRepository socialWorkerChatReadStatusRepository;
    private final CaregiverChatReadStatusRepository caregiverChatReadStatusRepository;
    private final CompletedMatchingRepository completedMatchingRepository;
    private final RecruitmentDomainService recruitmentDomainService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final ApplicationRepository applicationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /***
     * 2025-09-24
     * 3.1 공고 목록 (매칭 대기)
     * @return
     */
    @Transactional(readOnly = true)
    public Page<ElderlySimpleDto> getWaitingElderlys(Pageable pageable) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();

        return elderlyRepository
                .findAllWaitingMatching(loggedInSocialWorker.getNursingInstitution(), pageable)
                .map(ElderlySimpleDto::from);
    }

    @Transactional(readOnly = true)
    public Page<ElderlySimpleDto> searchWaitingElderlys(
            Pageable pageable, @Valid WaitingMatchingElderlySearchRequest request) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        return elderlyRepository
                .searchAllWaitingMatching(loggedInSocialWorker.getNursingInstitution(), pageable, request.keyword())
                .map(ElderlySimpleDto::from);
    }

    /***
     * 2025-10-08
     * 3.1 공고 목록 (매칭중 / 매칭완료)
     * @param elderlyMatchingStatusFilter
     * @return List<MatchingStatusSimpleResponse>
     */
    @Transactional(readOnly = true)
    public Page<SocialWorkerRecruitmentResponse> getRecruitmentList(
            ElderlyMatchingStatusFilter elderlyMatchingStatusFilter, Pageable pageable) {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();

        List<RecruitmentStatus> recruitmentStatus =
                switch (elderlyMatchingStatusFilter) {
                    case 매칭중 -> List.of(RecruitmentStatus.모집중);
                    case 매칭완료 -> List.of(RecruitmentStatus.모집완료, RecruitmentStatus.공고마감);
                    default -> throw new RecruitmentException("매칭 상태 필터가 잘못되었습니다 : " + elderlyMatchingStatusFilter);
                };

        List<WorkApplication> workApplications = workApplicationRepository.findAllActiveWorkApplication();

        return recruitmentRepository
                .findAllByInstitutionAndRecruitmentStatusIn(
                        socialworker.getNursingInstitution(), recruitmentStatus, pageable)
                .map(recruitment -> {
                    long applicationCount = applicationRepository.countByRecruitment(recruitment);
                    long matchingCount = workApplications.stream()
                            .filter(workApplication -> matchingDomainService.isMatched(workApplication, recruitment))
                            .count();

                    return SocialWorkerRecruitmentResponse.of(recruitment, applicationCount, matchingCount);
                });
    }

    @Transactional(readOnly = true)
    public Page<SocialWorkerRecruitmentResponse> searchRecruitmentList(
            ElderlyMatchingStatusFilter elderlyMatchingStatusFilter,
            Pageable pageable,
            MatchingRecruitmentSearchRequest request) {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();

        List<RecruitmentStatus> recruitmentStatus =
                switch (elderlyMatchingStatusFilter) {
                    case 매칭중 -> List.of(RecruitmentStatus.모집중);
                    case 매칭완료 -> List.of(RecruitmentStatus.모집완료, RecruitmentStatus.공고마감);
                    default -> throw new RecruitmentException("매칭 상태 필터가 잘못되었습니다 : " + elderlyMatchingStatusFilter);
                };

        List<WorkApplication> workApplications = workApplicationRepository.findAllActiveWorkApplication();

        return recruitmentRepository
                .searchByInstitutionAndElderlyNameOrRecruitmentTitle(
                        socialworker.getNursingInstitution(), recruitmentStatus, request.keyword(), pageable)
                .map(recruitment -> {
                    long applicationCount = applicationRepository.countByRecruitment(recruitment);
                    long matchingCount = workApplications.stream()
                            .filter(workApplication -> matchingDomainService.isMatched(workApplication, recruitment))
                            .count();

                    return SocialWorkerRecruitmentResponse.of(recruitment, applicationCount, matchingCount);
                });
    }

    public MatchingCaregiverDetailResponse getCaregiverDetailInfo(Long recruitmentId, Long caregiverId) {
        authUtil.getLoggedInSocialWorker();

        Caregiver caregiver = caregiverRepository
                .findById(caregiverId)
                .orElseThrow(() -> new RecruitmentException(CAREGIVER_NOT_EXISTS));
        WorkApplication workApplication = workApplicationRepository
                .findByCaregiver(caregiver)
                .orElseThrow(() -> new RecruitmentException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));
        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));

        Optional<Application> applicationOpt =
                applicationRepository.findByCaregiverAndRecruitment(caregiver, recruitment);
        List<MediationType> mediationTypes = applicationOpt
                .map(application -> application.getMediationTypes().stream().toList())
                .orElse(List.of());
        String mediationDescription =
                applicationOpt.map(Application::getMediationDescription).orElse("");

        Career career = careerRepository.findById(caregiverId).orElse(null);

        List<CareerDetail> careerDetails = careerDetailRepository.findAllByCareer(career);

        MatchingResultInfo matchingResultInfo =
                matchingDomainService.calculateMatchingResult(workApplication, recruitment);
        MatchingResultStatus matchingResultStatus =
                matchingDomainService.calculateMatchingStatus(workApplication, recruitment);

        return MatchingCaregiverDetailResponse.of(
                workApplication,
                matchingResultStatus,
                matchingResultInfo,
                career,
                careerDetails,
                mediationTypes,
                mediationDescription);
    }

    /**
     * 2025-10-09 Kwon Chan
     * 3.2.1.3 공고 등록 - 일정 중복 검증
     * @param request
     */
    @Transactional(readOnly = true)
    public void validateDuplicated(RecruitmentValidateDuplicatedRequest request) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        Elderly elderly = elderlyRepository
                .findById(request.elderlyId())
                .orElseThrow(() -> new ElderlyException(ELDERLY_NOT_EXISTS));

        elderlyDomainService.validateElderlyAndSocialWorkerInstitution(elderly, loggedInSocialWorker);

        List<CompletedMatching> completedMatchings = completedMatchingRepository.findAllByElderly(elderly);

        completedMatchings.forEach(completedMatching -> {
            Contract contract = completedMatching.getContract();
            if (Collections.disjoint(contract.getWorkDays(), request.workDays())) {
                return;
            }
            // TODO : Period 로 만들어서 오버랩 검증 로직 작성
            if (contract.getWorkEndTime().isBefore(request.workStartTime())
                    || request.workEndTime().isBefore(contract.getWorkStartTime())) {
                return;
            }
            throw new RecruitmentException(RECRUITMENT_WORK_TIME_DUPLICATED);
        });
    }

    /**
     * 2025-10-09 Kwon Chan
     * 3.2.1 매칭 공고 등록
     * @param request
     * @return Long recruitment id
     */
    @Transactional
    public Long createRecruitment(RecruitmentCreateRequest request) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();

        Elderly elderly = elderlyRepository
                .findById(request.elderlyId())
                .orElseThrow((() -> new RecruitmentException(ELDERLY_NOT_EXISTS)));
        elderlyDomainService.validateElderlyAndSocialWorkerInstitution(elderly, loggedInSocialWorker);

        Recruitment recruitment = Recruitment.create(request, elderly);
        recruitmentRepository.save(recruitment);

        return recruitment.getId();
    }

    /**
     * 2025-10-15
     * 3.1.4 매칭 공고 상세 정보 조회
     * @param recruitmentId
     * @return
     */
    @Transactional(readOnly = true)
    public RecruitmentDto getRecruitment(Long recruitmentId) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();

        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));

        recruitmentDomainService.validateRecruitmentInstitution(recruitment, loggedInSocialWorker);

        return RecruitmentDto.from(recruitment);
    }

    /**
     * 3.1.4 공고 마감 처리
     * @param recruitmentId 마감할 공고의 ID
     */
    @Transactional
    public void closeRecruitment(Long recruitmentId) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();

        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));

        recruitmentDomainService.validateRecruitmentInstitution(recruitment, loggedInSocialWorker);

        recruitment.close();

        // 매칭마다 chatRoom 조회 후 상태 변경
        notifyChatRoomsRecruitmentClosed(recruitmentId);
    }

    private void notifyChatRoomsRecruitmentClosed(Long recruitmentId) {
        ChatRoomActiveStatusUpdatedChatResponse chatResponse =
                ChatRoomActiveStatusUpdatedChatResponse.of(ChatRoomActiveStatus.공고마감);

        chatRoomRepository
                .findAllByChatRoomActiveStatusAndRecruitmentId(ChatRoomActiveStatus.채팅가능, recruitmentId)
                .forEach(chatRoom -> {
                    chatRoom.recruitmentClosed();
                    messagingTemplate.convertAndSend("/topic/chat-room/" + chatRoom.getId(), chatResponse);
                });
    }

    /**
     * 3.1.4 공고 상세 - 요양보호사 매칭 현황 조회
     * @param recruitmentId
     * @return
     */
    @Transactional(readOnly = true)
    public RecruitmentMatchingStatusResponse getMatchingStatus(Long recruitmentId) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));

        recruitmentDomainService.validateRecruitmentInstitution(recruitment, loggedInSocialWorker);

        List<MatchingCaregiverSimpleResponse> matchedCaregivers =
                workApplicationRepository.findAllActiveWorkApplication().stream()
                        .filter(workApplication -> matchingDomainService.isMatched(workApplication, recruitment))
                        .map(workApplication -> {
                            String careerTitle = careerRepository
                                    .findByCaregiver(workApplication.getCaregiver())
                                    .map(Career::getTitle)
                                    .orElse("경력서를 작성하지 않았습니다.");
                            MatchingResultStatus result =
                                    matchingDomainService.calculateMatchingStatus(workApplication, recruitment);
                            return MatchingCaregiverSimpleResponse.of(workApplication, result, careerTitle);
                        })
                        .toList();

        List<MatchingCaregiverSimpleResponse> appliedCaregivers =
                applicationRepository.findAllByRecruitment(recruitment).stream()
                        .map(Application::getWorkApplication)
                        .filter(workApplication -> matchingDomainService.isMatched(workApplication, recruitment))
                        .map(workApplication -> {
                            String careerTitle = careerRepository
                                    .findByCaregiver(workApplication.getCaregiver())
                                    .map(Career::getTitle)
                                    .orElse("경력서를 작성하지 않았습니다.");
                            MatchingResultStatus result =
                                    matchingDomainService.calculateMatchingStatus(workApplication, recruitment);
                            return MatchingCaregiverSimpleResponse.of(workApplication, result, careerTitle);
                        })
                        .toList();

        return RecruitmentMatchingStatusResponse.of(recruitment, matchedCaregivers, appliedCaregivers);
    }

    /**
     * 3.1.4 공고 상세 - 공고 수정
     * @param recruitmentId 공고 ID
     */
    @Transactional
    public void updateRecruitment(Long recruitmentId, RecruitmentUpdateRequest request) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();

        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new DomainException(RECRUITMENT_NOT_EXISTS));

        boolean isApplicantOrProcessingContractExists = applicationRepository.existsByRecruitment(recruitment)
                || chatRoomRepository.existsByRecruitment(recruitment);

        recruitmentDomainService.validateRecruitmentInstitution(recruitment, loggedInSocialWorker);
        recruitmentDomainService.validateRecruitmentUpdatable(recruitment, isApplicantOrProcessingContractExists);

        recruitment.update(
                request.title(),
                request.workDays(),
                request.workStartTime(),
                request.workEndTime(),
                request.careTypes(),
                request.workSalaryUnitType(),
                request.workSalaryAmount(),
                request.description());
    }

    /**
     * 3.1.4 공고 삭제 - 지원자가 없을 때만 공고 삭제 가능
     * @param recruitmentId 공고 ID
     */
    @Transactional
    public void deleteRecruitment(Long recruitmentId) {
        SocialWorker socialWorker = authUtil.getLoggedInSocialWorker();
        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new DomainException(RECRUITMENT_NOT_EXISTS));

        boolean isApplicantOrProcessingContractExists = applicationRepository.existsByRecruitment(recruitment)
                || chatRoomRepository.existsByRecruitment(recruitment);

        recruitmentDomainService.validateRecruitmentInstitution(recruitment, socialWorker);
        recruitmentDomainService.validateRecruitmentDeletable(recruitment, isApplicantOrProcessingContractExists);

        recruitmentRepository.delete(recruitment);
    }

    @Transactional
    public long proposeWork(Long recruitmentId, Long caregiverId, LocalDate workStartDate) {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();

        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));

        Caregiver caregiver = caregiverRepository
                .findById(caregiverId)
                .orElseThrow(() -> new CaregiverException(CAREGIVER_NOT_EXISTS));

        applicationRepository
                .findByCaregiverAndRecruitment(caregiver, recruitment)
                .ifPresent(Application::propose);

        return initChatRoomAndChatReadStatuses(
                recruitment, caregiver, socialworker.getNursingInstitution(), workStartDate);
    }

    private long initChatRoomAndChatReadStatuses(
            Recruitment recruitment, Caregiver caregiver, NursingInstitution institution, LocalDate workStartDate) {
        ChatRoom newChatRoom = ChatRoom.create(recruitment);
        chatRoomRepository.save(newChatRoom);

        // Caregiver 상태 생성
        CaregiverChatReadStatus caregiverChatReadStatus = CaregiverChatReadStatus.create(caregiver, newChatRoom);
        caregiverChatReadStatusRepository.save(caregiverChatReadStatus);

        // SocialWorker 상태 생성
        List<SocialWorker> socialWorkers = socialWorkerRepository.findAllByNursingInstitution(institution);
        List<SocialWorkerChatReadStatus> socialWorkerChatReadStatuses = socialWorkers.stream()
                .map(s -> SocialWorkerChatReadStatus.create(s, newChatRoom))
                .toList();
        socialWorkerChatReadStatusRepository.saveAll(socialWorkerChatReadStatuses);

        // 최초 계약서채팅 생성
        Contract contract =
                Contract.create(newChatRoom, recruitment.getElderly(), caregiver, recruitment, workStartDate);
        chatRepository.save(contract);

        return newChatRoom.getId();
    }

    @Transactional
    public void postponeApplicationDecision(Long applicationId) {
        SocialWorker socialWorker = authUtil.getLoggedInSocialWorker();

        Application application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new DomainException(APPLICATION_NOT_EXISTS));

        recruitmentDomainService.validateRecruitmentInstitution(application.getRecruitment(), socialWorker);
        application.postpone();
    }

    @Transactional
    public void resumeApplicationDecision(Long applicationId) {
        SocialWorker socialWorker = authUtil.getLoggedInSocialWorker();

        Application application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new DomainException(APPLICATION_NOT_EXISTS));

        recruitmentDomainService.validateRecruitmentInstitution(application.getRecruitment(), socialWorker);
        application.resume();
    }
}
