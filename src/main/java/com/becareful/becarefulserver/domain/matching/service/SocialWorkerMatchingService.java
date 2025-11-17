package com.becareful.becarefulserver.domain.matching.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.caregiver.repository.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.repository.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.service.MatchingDomainService;
import com.becareful.becarefulserver.domain.matching.domain.service.RecruitmentDomainService;
import com.becareful.becarefulserver.domain.matching.domain.vo.*;
import com.becareful.becarefulserver.domain.matching.dto.*;
import com.becareful.becarefulserver.domain.matching.dto.request.*;
import com.becareful.becarefulserver.domain.matching.dto.response.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
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
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialWorkerMatchingService {

    private final AuthUtil authUtil;
    private final ElderlyDomainService elderlyDomainService;
    private final MatchingDomainService matchingDomainService;
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
    private final CompletedMatchingRepository completedMatchingRepository;
    private final RecruitmentDomainService recruitmentDomainService;

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

        return recruitmentRepository.findAllByInstitution(
                socialworker.getNursingInstitution(), recruitmentStatus, pageable);
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

        return recruitmentRepository.searchByInstitutionAndElderlyNameOrRecruitmentTitle(
                socialworker.getNursingInstitution(), recruitmentStatus, request.keyword(), pageable);
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

        Matching matching = matchingRepository
                .findByWorkApplicationAndRecruitment(workApplication, recruitment)
                .orElseThrow(() -> new RecruitmentException(MATCHING_NOT_EXISTS));

        Career career = careerRepository.findById(caregiverId).orElse(null);

        List<CareerDetail> careerDetails = careerDetailRepository.findAllByCareer(career);

        return MatchingCaregiverDetailResponse.of(matching, career, careerDetails);
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
            Recruitment recruitment =
                    completedMatching.getContract().getMatching().getRecruitment();
            if (Collections.disjoint(recruitment.getWorkDays(), request.workDays())) {
                return;
            }
            // TODO : Period 로 만들어서 오버랩 검증 로직 작성
            if (recruitment.getWorkEndTime().isBefore(request.workStartTime())
                    || request.workEndTime().isBefore(recruitment.getWorkStartTime())) {
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
        Elderly elderly = elderlyRepository
                .findById(request.elderlyId())
                .orElseThrow((() -> new RecruitmentException(ELDERLY_NOT_EXISTS)));

        Recruitment recruitment = Recruitment.create(request, elderly);
        recruitmentRepository.save(recruitment);

        workApplicationRepository.findAllActiveWorkApplication().forEach(workApplication -> {
            matchingDomainService.createMatching(recruitment, workApplication).ifPresent(matchingRepository::save);
        });

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

        List<Matching> matchings = matchingRepository.findAllByRecruitment(recruitment);

        List<MatchingCaregiverSimpleResponse> unAppliedCaregivers = new ArrayList<>();
        List<MatchingCaregiverSimpleResponse> appliedCaregivers = new ArrayList<>();

        matchings.forEach(matching -> {
            MatchingApplicationStatus applicationStatus = matching.getApplicationStatus();
            Caregiver caregiver = matching.getWorkApplication().getCaregiver();
            String careerTitle = careerRepository
                    .findByCaregiver(caregiver)
                    .map(Career::getTitle)
                    .orElse("경력서를 작성하지 않았습니다.");

            MatchedCaregiverResponse caregiverInfo = MatchedCaregiverResponse.of(caregiver, careerTitle);
            MatchingResultStatus matchingResult = matching.getMatchingResultStatus();

            var matchedCaregiverInfo = MatchingCaregiverSimpleResponse.of(caregiverInfo, matchingResult);

            switch (applicationStatus) {
                case 미지원 -> unAppliedCaregivers.add(matchedCaregiverInfo);
                case 지원 -> appliedCaregivers.add(matchedCaregiverInfo);
            }
        });

        return RecruitmentMatchingStatusResponse.of(recruitment, unAppliedCaregivers, appliedCaregivers);
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

        boolean isApplicantOrProcessingContractExists =
                matchingRepository.existsByApplicantOrProcessingContract(recruitment);

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

        matchingRepository.deleteAllByRecruitment(recruitment);
        workApplicationRepository.findAllActiveWorkApplication().forEach(workApplication -> {
            matchingDomainService.createMatching(recruitment, workApplication).ifPresent(matchingRepository::save);
        });
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

        boolean isApplicantOrProcessingContractExists =
                matchingRepository.existsByApplicantOrProcessingContract(recruitment);

        recruitmentDomainService.validateRecruitmentInstitution(recruitment, socialWorker);
        recruitmentDomainService.validateRecruitmentDeletable(recruitment, isApplicantOrProcessingContractExists);

        matchingRepository.deleteAllByRecruitment(recruitment);
        recruitmentRepository.delete(recruitment);
    }

    @Transactional
    public void propose(Long recruitmentId, Long caregiverId, LocalDate workStartDate) {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();

        Matching matching = matchingRepository
                .findByCaregiverIdAndRecruitmentId(caregiverId, recruitmentId)
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
}
