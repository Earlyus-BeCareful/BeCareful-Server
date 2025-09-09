package com.becareful.becarefulserver.domain.matching.service;

import static com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus.*;
import static com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus.*;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.caregiver.repository.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.repository.*;
import com.becareful.becarefulserver.domain.chat.service.*;
import com.becareful.becarefulserver.domain.common.domain.vo.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.vo.*;
import com.becareful.becarefulserver.domain.matching.dto.*;
import com.becareful.becarefulserver.domain.matching.dto.request.*;
import com.becareful.becarefulserver.domain.matching.dto.response.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.domain.socialworker.repository.*;
import com.becareful.becarefulserver.domain.work_location.dto.request.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import com.becareful.becarefulserver.global.util.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final AuthUtil authUtil;
    private final MatchingRepository matchingRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final WorkApplicationWorkLocationRepository workApplicationWorkLocationRepository;
    private final ElderlyRepository elderlyRepository;
    private final WorkApplicationRepository workApplicationRepository;
    private final CareerRepository careerRepository;
    private final CaregiverRepository caregiverRepository;
    private final CareerDetailRepository careerDetailRepository;
    private final CaregiverChatService caregiverChatService;
    private final ContractService contractService;
    private final SocialWorkerRepository socialWorkerRepository;
    private final SocialWorkerChatReadStatusRepository socialWorkerChatReadStatusRepository;
    private final CaregiverChatReadStatusRepository caregiverChatReadStatusRepository;

    public MatchingCaregiverDetailResponse getCaregiverDetailInfo(Long recruitmentId, Long caregiverId) {
        authUtil.getLoggedInSocialWorker(); // 사회복지사가 호출하는 API

        Caregiver caregiver = caregiverRepository
                .findById(caregiverId)
                .orElseThrow(() -> new RecruitmentException(CAREGIVER_NOT_EXISTS));
        WorkApplication workApplication = workApplicationRepository
                .findByCaregiver(caregiver)
                .orElseThrow(() -> new RecruitmentException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));
        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));
        List<WorkLocationDto> locations =
                workApplicationWorkLocationRepository.findAllByWorkApplication(workApplication).stream()
                        .map(workLocation -> WorkLocationDto.from(workLocation.getWorkLocation()))
                        .toList();

        Matching matching = matchingRepository
                .findByWorkApplicationAndRecruitment(workApplication, recruitment)
                .orElseThrow(() -> new RecruitmentException(MATCHING_NOT_EXISTS));

        Career career = careerRepository.findById(caregiverId).orElse(null);

        List<CareerDetail> careerDetails = careerDetailRepository.findAllByCareer(career);

        return MatchingCaregiverDetailResponse.of(matching, career, careerDetails, locations);
    }

    public List<RecruitmentListItemResponse> getCaregiverMatchingRecruitmentList() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        return workApplicationRepository
                .findByCaregiver(caregiver)
                .map(workApplication ->
                        matchingRepository.findAllByCaregiverAndApplicationStatus(caregiver, 미지원).stream()
                                .map(RecruitmentListItemResponse::from)
                                .toList())
                .orElse(null);
    }

    public RecruitmentDetailResponse getRecruitmentDetail(Long recruitmentId) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        Matching matching = matchingRepository
                .findByCaregiverAndRecruitmentId(caregiver, recruitmentId)
                .orElseThrow(() -> new MatchingException(MATCHING_NOT_EXISTS));

        boolean hasNewChat = caregiverChatService.checkNewChat(caregiver);

        // TODO : recruit 매칭 적합도 및 태그 부여 판단
        return RecruitmentDetailResponse.from(matching, false, false, hasNewChat);
    }

    public CaregiverAppliedMatchingRecruitmentsResponse getMyRecruitment(
            MatchingApplicationStatus matchingApplicationStatus) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();

        List<CaregiverAppliedMatchingRecruitmentsResponse.Item> recruitments = workApplicationRepository
                .findByCaregiver(caregiver)
                .map(
                        workApplication -> matchingRepository
                                .findByWorkApplicationAndMatchingApplicationStatus(
                                        workApplication, matchingApplicationStatus)
                                .stream()
                                .map(CaregiverAppliedMatchingRecruitmentsResponse.Item::from)
                                .toList())
                .orElse(List.of());
        boolean hasNewChat = caregiverChatService.checkNewChat(caregiver);
        return CaregiverAppliedMatchingRecruitmentsResponse.of(recruitments, hasNewChat);
    }

    public CaregiverAppliedMatchingDetailResponse getMyRecruitmentDetail(Long recruitmentId) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));
        WorkApplication workApplication = workApplicationRepository
                .findByCaregiver(caregiver)
                .orElseThrow(() -> new RecruitmentException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));

        Matching matching = matchingRepository
                .findByWorkApplicationAndRecruitment(workApplication, recruitment)
                .orElseThrow(() -> new RecruitmentException(MATCHING_NOT_EXISTS));

        boolean hasNewChat = caregiverChatService.checkNewChat(caregiver);

        return CaregiverAppliedMatchingDetailResponse.of(matching, false, false, hasNewChat);
    }

    @Transactional
    public void mediateMatching(Long recruitmentId, RecruitmentMediateRequest request) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));
        WorkApplication workApplication = workApplicationRepository
                .findByCaregiver(caregiver)
                .orElseThrow(() -> new RecruitmentException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));

        Matching matching = matchingRepository
                .findByWorkApplicationAndRecruitment(workApplication, recruitment)
                .orElseThrow(() -> new RecruitmentException(MATCHING_NOT_EXISTS));

        matching.mediate(request);
    }

    @Transactional
    public void rejectMatching(Long recruitmentId) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));
        WorkApplication workApplication = workApplicationRepository
                .findByCaregiver(caregiver)
                .orElseThrow(() -> new RecruitmentException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));

        Matching matching = matchingRepository
                .findByWorkApplicationAndRecruitment(workApplication, recruitment)
                .orElseThrow(() -> new RecruitmentException(MATCHING_NOT_EXISTS));

        matching.reject();
    }

    @Transactional
    public void applyRecruitment(Long recruitmentId) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));
        WorkApplication workApplication = workApplicationRepository
                .findByCaregiver(caregiver)
                .orElseThrow(() -> new RecruitmentException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));

        Matching matching = matchingRepository
                .findByWorkApplicationAndRecruitment(workApplication, recruitment)
                .orElseThrow(() -> new RecruitmentException(MATCHING_NOT_EXISTS));

        matching.apply();
    }

    @Transactional
    public Long createRecruitment(RecruitmentCreateRequest request) {
        Elderly elderly = elderlyRepository
                .findById(request.elderlyId())
                .orElseThrow((() -> new RecruitmentException(ELDERLY_NOT_EXISTS)));

        Recruitment recruitment = Recruitment.create(request, elderly);
        recruitmentRepository.save(recruitment);

        // TODO 이미 근무 중인 데이터와 일정이 겹치는지 체크
        matchingWith(recruitment);

        return recruitment.getId();
    }

    public List<MatchingStatusSimpleResponse> getMatchingList(MatchingStatusFilter matchingStatusFilter) {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();
        List<Recruitment> recruitments = recruitmentRepository.findAllByInstitutionId(
                socialworker.getNursingInstitution().getId());

        return recruitments.stream()
                .filter(recruitment -> {
                    if (matchingStatusFilter.equals(MatchingStatusFilter.진행중)) {
                        return recruitment.isRecruiting();
                    }
                    if (matchingStatusFilter.equals(MatchingStatusFilter.완료)) {
                        return !recruitment.isRecruiting();
                    }
                    return true;
                })
                .map(recruitment -> {
                    int notAppliedMatchingCount = matchingRepository.countByRecruitmentAndMatchingApplicationStatus(
                            recruitment, 미지원); // 거절 제거 할래말래
                    int appliedMatchingCount =
                            matchingRepository.countByRecruitmentAndMatchingApplicationStatus(recruitment, 지원검토중);

                    return MatchingStatusSimpleResponse.of(recruitment, notAppliedMatchingCount, appliedMatchingCount);
                })
                .toList();
    }

    // 매칭 상세 - 공고 상세 페이지
    public MatchingStatusDetailResponse getMatchingDetail(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));
        List<Matching> matchings = matchingRepository.findAllByRecruitment(recruitment);

        List<MatchingCaregiverSimpleResponse> unAppliedCaregivers = new ArrayList<>();
        List<MatchingCaregiverSimpleResponse> appliedCaregivers = new ArrayList<>();

        matchings.forEach(matching -> {

            MatchingApplicationStatus status = matching.getMatchingApplicationStatus();

            if (status == 지원검토중 || status == 미지원) {
                WorkApplication workApplication = matching.getWorkApplication();
                if (workApplication == null) {
                    return; // 요양보호사가 탈퇴하며 지원검토중, 미지원인 매칭은 삭제되지만 방어적 설계를 위해 예외처리함
                }

                Caregiver caregiver = matching.getWorkApplication().getCaregiver();
                Career career = careerRepository
                        .findByCaregiver(caregiver)
                        .orElseThrow(() -> new CaregiverException(CAREGIVER_CAREER_NOT_EXISTS));

                MatchedCaregiverDto caregiverInfo = MatchedCaregiverDto.of(caregiver, career);
                MatchingResultStatus matchingResult =
                        matching.getMatchingResultInfo().judgeMatchingResultStatus();

                var matchedCaregiverInfo = MatchingCaregiverSimpleResponse.of(caregiverInfo, matchingResult);

                if (status == 지원검토중) {
                    appliedCaregivers.add(matchedCaregiverInfo);
                } else {
                    unAppliedCaregivers.add(matchedCaregiverInfo);
                }
            }
        });

        return MatchingStatusDetailResponse.of(recruitment, unAppliedCaregivers, appliedCaregivers);
    }

    @Transactional
    public void hire(Long matchingId, LocalDate workStartDate) {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();

        Matching matching = matchingRepository
                .findByIdWithRecruitment(matchingId)
                .orElseThrow(() -> new MatchingException(MATCHING_NOT_EXISTS));

        initChatReadStatuses(matching, socialworker);
        contractService.createContract(matching, workStartDate);
    }

    private void initChatReadStatuses(Matching matching, SocialWorker loggedInSocialWorker) {
        // Caregiver 상태 생성
        Caregiver caregiver = matching.getWorkApplication().getCaregiver();
        CaregiverChatReadStatus caregiverStatus = CaregiverChatReadStatus.create(caregiver, matching);
        caregiverChatReadStatusRepository.save(caregiverStatus);
        // SocialWorker 상태 생성
        List<SocialWorker> socialWorkers =
                socialWorkerRepository.findAllByNursingInstitution(loggedInSocialWorker.getNursingInstitution());
        List<SocialWorkerChatReadStatus> socialWorkerChatReadStatuses = socialWorkers.stream()
                .map(s -> SocialWorkerChatReadStatus.create(s, matching))
                .toList();
        socialWorkerChatReadStatusRepository.saveAll(socialWorkerChatReadStatuses);
    }

    private void matchingWith(Recruitment recruitment) {
        workApplicationRepository.findAllActiveWorkApplication().stream()
                .map(application -> {
                    List<Location> locations =
                            workApplicationWorkLocationRepository.findAllByWorkApplication(application).stream()
                                    .map(WorkApplicationWorkLocation::getLocation)
                                    .toList();

                    return Matching.create(recruitment, application, locations);
                })
                .filter((matching -> !matching.getMatchingResultStatus().equals(제외)))
                .forEach(matchingRepository::save);
    }
}
