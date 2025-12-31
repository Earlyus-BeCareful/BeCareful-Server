package com.becareful.becarefulserver.domain.matching.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationRepository;
import com.becareful.becarefulserver.domain.chat.repository.CaregiverChatReadStatusRepository;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.service.MatchingDomainService;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultInfo;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.matching.dto.request.CaregiverAppliedStatusFilter;
import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentMediateRequest;
import com.becareful.becarefulserver.domain.matching.dto.response.CaregiverAppliedMatchingDetailResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.CaregiverAppliedRecruitmentsResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.CaregiverRecruitmentResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.RecruitmentDetailResponse;
import com.becareful.becarefulserver.domain.matching.repository.ApplicationRepository;
import com.becareful.becarefulserver.domain.matching.repository.RecruitmentRepository;
import com.becareful.becarefulserver.global.exception.exception.DomainException;
import com.becareful.becarefulserver.global.exception.exception.RecruitmentException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaregiverMatchingService {

    private final AuthUtil authUtil;
    private final WorkApplicationRepository workApplicationRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final ApplicationRepository applicationRepository;
    private final MatchingDomainService matchingDomainService;
    private final CaregiverChatReadStatusRepository caregiverChatReadStatusRepository;

    @Transactional(readOnly = true)
    public List<CaregiverRecruitmentResponse> getCaregiverMatchingRecruitmentList() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        return workApplicationRepository
                .findByCaregiver(caregiver)
                .map(workApplication -> recruitmentRepository.findAll().stream()
                        .filter(recruitment -> matchingDomainService.isMatched(workApplication, recruitment))
                        .map(recruitment -> CaregiverRecruitmentResponse.of(
                                recruitment,
                                matchingDomainService.calculateMatchingStatus(workApplication, recruitment),
                                matchingDomainService.calculateMatchingResult(workApplication, recruitment)))
                        .toList())
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public RecruitmentDetailResponse getRecruitmentDetail(Long recruitmentId) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        WorkApplication workApplication = workApplicationRepository
                .findByCaregiver(caregiver)
                .orElseThrow(() -> new DomainException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));
        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));

        MatchingResultStatus result = matchingDomainService.calculateMatchingStatus(workApplication, recruitment);

        // TODO : recruitment 태그 부여 판단
        return RecruitmentDetailResponse.of(recruitment, result, false, false);
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

        Application application = Application.general(recruitment, workApplication);
        applicationRepository.save(application);
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

        Application application = Application.mediated(
                recruitment, workApplication, request.mediationTypes(), request.mediationDescription());
        applicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public CaregiverAppliedRecruitmentsResponse getMyAppliedRecruitment(CaregiverAppliedStatusFilter appliedStatus) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();

        boolean isShouldBeRecruiting = (appliedStatus == CaregiverAppliedStatusFilter.검토중);

        List<ApplicationStatus> applicationStatuses =
                switch (appliedStatus) {
                    case 검토중 -> List.of(ApplicationStatus.지원검토, ApplicationStatus.근무제안);
                    case 합격 -> List.of(ApplicationStatus.채용완료);
                    case 마감 -> List.of(ApplicationStatus.지원검토, ApplicationStatus.근무제안, ApplicationStatus.채용불발);
                };

        List<CaregiverRecruitmentResponse> recruitments =
                applicationRepository
                        .findAllByCaregiverAndApplicationStatusIn(caregiver, applicationStatuses, isShouldBeRecruiting)
                        .stream()
                        .map(application -> {
                            Recruitment recruitment = application.getRecruitment();
                            MatchingResultStatus result = matchingDomainService.calculateMatchingStatus(
                                    application.getWorkApplication(), recruitment);
                            MatchingResultInfo matchingResultInfo = matchingDomainService.calculateMatchingResult(
                                    application.getWorkApplication(), recruitment);
                            return CaregiverRecruitmentResponse.of(recruitment, result, matchingResultInfo);
                        })
                        .toList();
        return CaregiverAppliedRecruitmentsResponse.of(recruitments);
    }

    @Transactional(readOnly = true)
    public CaregiverAppliedMatchingDetailResponse getMyAppliedRecruitmentDetail(Long recruitmentId) {
        // TODO : application id 받아서 찾기
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));

        Application application = applicationRepository
                .findByCaregiverAndRecruitment(caregiver, recruitment)
                .orElseThrow(() -> new DomainException(APPLICATION_NOT_EXISTS));

        MatchingResultStatus result =
                matchingDomainService.calculateMatchingStatus(application.getWorkApplication(), recruitment);

        Long chatRoomId = caregiverChatReadStatusRepository
                .findChatRoomIdByCaregiverAndRecruitment(caregiver, recruitment)
                .orElse(null);

        return CaregiverAppliedMatchingDetailResponse.of(application, result, chatRoomId, false, false);
    }
}
