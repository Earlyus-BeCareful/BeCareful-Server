package com.becareful.becarefulserver.domain.matching.service;

import static com.becareful.becarefulserver.domain.matching.domain.MatchingStatus.*;
import static com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus.*;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.caregiver.repository.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.repository.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.vo.*;
import com.becareful.becarefulserver.domain.matching.dto.*;
import com.becareful.becarefulserver.domain.matching.dto.request.*;
import com.becareful.becarefulserver.domain.matching.dto.response.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.domain.socialworker.repository.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import com.becareful.becarefulserver.global.util.*;
import jakarta.validation.Valid;
import java.time.*;
import java.util.*;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialWorkerMatchingService {

    private final AuthUtil authUtil;
    private final MatchingRepository matchingRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final ElderlyRepository elderlyRepository;
    private final WorkApplicationRepository workApplicationRepository;
    private final CareerRepository careerRepository;
    private final CaregiverRepository caregiverRepository;
    private final CareerDetailRepository careerDetailRepository;
    private final ContractRepository contractRepository;
    private final SocialWorkerRepository socialWorkerRepository;
    private final SocialWorkerChatReadStatusRepository socialWorkerChatReadStatusRepository;
    private final CaregiverChatReadStatusRepository caregiverChatReadStatusRepository;

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

    public Page<ElderlySimpleDto> searchWaitingElderlys(
            Pageable pageable, @Valid WaitingMatchingElderlySearchRequest request) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        return elderlyRepository
                .searchAllWaitingMatching(loggedInSocialWorker.getNursingInstitution(), pageable, request.keyword())
                .map(ElderlySimpleDto::from);
    }

    /***
     * 2025-09-24
     * 3.1 공고 목록 (매칭중 / 매칭완료)
     * @param elderlyMatchingStatusFilter
     * @return List<MatchingStatusSimpleResponse>
     */
    @Transactional(readOnly = true)
    public Page<SocialWorkerRecruitmentResponse> getRecruitmentList(
            ElderlyMatchingStatusFilter elderlyMatchingStatusFilter, Pageable pageable) {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();

        List<SocialWorkerRecruitmentResponse> recruitments =
                recruitmentRepository.findAllByInstitution(socialworker.getNursingInstitution()).stream()
                        .filter(recruitment -> {
                            if (elderlyMatchingStatusFilter.isMatchingProcessing()) {
                                return recruitment.getRecruitmentStatus().isRecruiting();
                            }
                            if (elderlyMatchingStatusFilter.isMatchingCompleted()) {
                                return recruitment.getRecruitmentStatus().isCompleted();
                            }
                            return false;
                        })
                        .map(recruitment -> {
                            int matchingCount = matchingRepository.countByRecruitment(recruitment); // 거절 제거 할래말래
                            int appliedMatchingCount =
                                    matchingRepository.countByRecruitmentAndMatchingStatus(recruitment, 지원검토중);

                            return SocialWorkerRecruitmentResponse.of(recruitment, matchingCount, appliedMatchingCount);
                        })
                        .toList();

        return new PageImpl<>(recruitments, pageable, recruitments.size());
    }

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

        Matching matching = matchingRepository
                .findByWorkApplicationAndRecruitment(workApplication, recruitment)
                .orElseThrow(() -> new RecruitmentException(MATCHING_NOT_EXISTS));

        Career career = careerRepository.findById(caregiverId).orElse(null);

        List<CareerDetail> careerDetails = careerDetailRepository.findAllByCareer(career);

        return MatchingCaregiverDetailResponse.of(matching, career, careerDetails);
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

    // 매칭 상세 - 공고 상세 페이지
    public MatchingStatusDetailResponse getMatchingDetail(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));
        List<Matching> matchings = matchingRepository.findAllByRecruitment(recruitment);

        List<MatchingCaregiverSimpleResponse> unAppliedCaregivers = new ArrayList<>();
        List<MatchingCaregiverSimpleResponse> appliedCaregivers = new ArrayList<>();

        matchings.forEach(matching -> {
            MatchingStatus status = matching.getMatchingStatus();

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
    public void propose(Long matchingId, LocalDate workStartDate) {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();

        Matching matching = matchingRepository
                .findByIdWithRecruitment(matchingId)
                .orElseThrow(() -> new MatchingException(MATCHING_NOT_EXISTS));

        matching.propose();

        initChatReadStatuses(matching, socialworker);

        Contract contract = Contract.create(matching, workStartDate);
        contractRepository.save(contract);
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
                .map(application -> Matching.create(recruitment, application))
                .filter((matching -> !matching.getMatchingResultStatus().equals(제외)))
                .forEach(matchingRepository::save);
    }
}
