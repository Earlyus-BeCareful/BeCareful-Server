package com.becareful.becarefulserver.domain.matching.service;

import static com.becareful.becarefulserver.domain.matching.domain.MatchingStatus.미지원;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationRepository;
import com.becareful.becarefulserver.domain.chat.repository.CaregiverChatReadStatusRepository;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.MatchingStatus;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentMediateRequest;
import com.becareful.becarefulserver.domain.matching.dto.response.CaregiverAppliedMatchingDetailResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.CaregiverAppliedRecruitmentsResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.CaregiverRecruitmentResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.RecruitmentDetailResponse;
import com.becareful.becarefulserver.domain.matching.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.matching.repository.RecruitmentRepository;
import com.becareful.becarefulserver.global.exception.exception.MatchingException;
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
    private final MatchingRepository matchingRepository;
    private final CaregiverChatReadStatusRepository chatReadStatusRepository;
    private final RecruitmentRepository recruitmentRepository;

    @Transactional(readOnly = true)
    public List<CaregiverRecruitmentResponse> getCaregiverMatchingRecruitmentList() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        return workApplicationRepository
                .findByCaregiver(caregiver)
                .map(workApplication ->
                        matchingRepository.findAllByCaregiverAndApplicationStatus(caregiver, 미지원).stream()
                                .map(CaregiverRecruitmentResponse::from)
                                .toList())
                .orElse(null);
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

    @Transactional(readOnly = true)
    public RecruitmentDetailResponse getRecruitmentDetail(Long recruitmentId) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        Matching matching = matchingRepository
                .findByCaregiverAndRecruitmentId(caregiver, recruitmentId)
                .orElseThrow(() -> new MatchingException(MATCHING_NOT_EXISTS));

        boolean hasNewChat = chatReadStatusRepository.existsUnreadChat(caregiver.getId());

        // TODO : recruit 매칭 적합도 및 태그 부여 판단
        return RecruitmentDetailResponse.from(matching, false, false, hasNewChat);
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

    @Transactional(readOnly = true)
    public CaregiverAppliedRecruitmentsResponse getMyAppliedRecruitment(MatchingStatus matchingStatus) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();

        List<CaregiverAppliedRecruitmentsResponse.Item> recruitments = workApplicationRepository
                .findByCaregiver(caregiver)
                .map(workApplication ->
                        matchingRepository
                                .findByWorkApplicationAndMatchingStatus(workApplication, matchingStatus)
                                .stream()
                                .map(CaregiverAppliedRecruitmentsResponse.Item::from)
                                .toList())
                .orElse(List.of());
        boolean hasNewChat = chatReadStatusRepository.existsUnreadChat(caregiver.getId());
        return CaregiverAppliedRecruitmentsResponse.of(recruitments, hasNewChat);
    }

    @Transactional(readOnly = true)
    public CaregiverAppliedMatchingDetailResponse getMyAppliedRecruitmentDetail(Long recruitmentId) {
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

        boolean hasNewChat = chatReadStatusRepository.existsUnreadChat(caregiver.getId());

        return CaregiverAppliedMatchingDetailResponse.of(matching, false, false, hasNewChat);
    }
}
