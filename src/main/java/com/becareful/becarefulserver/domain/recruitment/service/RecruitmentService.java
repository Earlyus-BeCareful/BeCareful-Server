package com.becareful.becarefulserver.domain.recruitment.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.CAREGIVER_WORK_APPLICATION_NOT_EXISTS;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.ELDERLY_NOT_EXISTS;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.MATCHING_NOT_EXISTS;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.RECRUITMENT_NOT_EXISTS;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplicationWorkLocation;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationWorkLocationRepository;
import com.becareful.becarefulserver.domain.recruitment.domain.Matching;
import com.becareful.becarefulserver.domain.recruitment.domain.Recruitment;
import com.becareful.becarefulserver.domain.recruitment.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.recruitment.dto.response.RecruitmentDetailResponse;
import com.becareful.becarefulserver.domain.recruitment.dto.response.RecruitmentResponse;
import com.becareful.becarefulserver.domain.recruitment.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.recruitment.repository.RecruitmentRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.global.exception.exception.RecruitmentException;
import com.becareful.becarefulserver.global.util.AuthUtil;

import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final WorkApplicationWorkLocationRepository workApplicationWorkLocationRepository;
    private final ElderlyRepository elderlyRepository;
    private final MatchingRepository matchingRepository;
    private final AuthUtil authUtil;
    private final WorkApplicationRepository workApplicationRepository;

    public List<RecruitmentResponse> getRecruitmentList() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        return workApplicationRepository.findByCaregiver(caregiver)
                .map(workApplication -> matchingRepository.findAllRecruitmentByWorkApplication(workApplication)
                        .stream().map(RecruitmentResponse::from).toList())
                .orElse(null);
    }

    public RecruitmentDetailResponse getRecruitmentDetail(Long recruitmentId) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));

        // TODO : recruit 매칭 적합도 및 태그 부여 판단
        return RecruitmentDetailResponse.from(recruitment, false, false, 98);
    }

    @Transactional
    public void rejectMatching(Long recruitmentId) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));
        WorkApplication workApplication = workApplicationRepository.findByCaregiver(caregiver)
                .orElseThrow(() -> new RecruitmentException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));

        Matching matching = matchingRepository.findByWorkApplicationAndRecruitment(workApplication, recruitment)
                .orElseThrow(() -> new RecruitmentException(MATCHING_NOT_EXISTS));

        matching.reject();
    }

    @Transactional
    public void applyRecruitment(Long recruitmentId) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));
        WorkApplication workApplication = workApplicationRepository.findByCaregiver(caregiver)
                .orElseThrow(() -> new RecruitmentException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));

        Matching matching = matchingRepository.findByWorkApplicationAndRecruitment(workApplication, recruitment)
                .orElseThrow(() -> new RecruitmentException(MATCHING_NOT_EXISTS));

        matching.apply();
    }

    @Transactional
    public Long createRecruitment(RecruitmentCreateRequest request) {
        Elderly elderly = elderlyRepository.findById(request.elderlyId())
                .orElseThrow((() -> new RecruitmentException(ELDERLY_NOT_EXISTS)));

        Recruitment recruitment = Recruitment.create(request, elderly);
        recruitmentRepository.save(recruitment);

        matchingWith(recruitment);

        return recruitment.getId();
    }

    private void matchingWith(Recruitment recruitment) {
        workApplicationWorkLocationRepository.findAllActiveWorkApplication().stream()
                .filter((application -> isMatched(recruitment, application)))
                .forEach(wa ->
                        matchingRepository.save(
                                Matching.create(recruitment, wa.getWorkApplication())));
    }

    private boolean isMatched(Recruitment recruitment,
            WorkApplicationWorkLocation workApplicationWorkLocation) {
        if (isNotMatchedWithWorkTime()) {
            return false;
        }

        if (isNotMatchedWithDaysOfWeek()) {
            return false;
        }

        if (isNotMatchedWithWorkSalary()) {
            return false;
        }

        if (isNotMatchedWithWorkLocation()) {
            return false;
        }

        if (isNotMatchedWithCareType()) {
            return false;
        }

        return true;
    }

    private boolean isNotMatchedWithDaysOfWeek() {
        // TODO
        return false;
    }

    private boolean isNotMatchedWithWorkTime() {
        // TODO
        return false;
    }

    private boolean isNotMatchedWithWorkLocation() {
        // TODO
        return false;
    }

    private boolean isNotMatchedWithWorkSalary() {
        // TODO
        return false;
    }

    private boolean isNotMatchedWithCareType() {
        // TODO
        return false;
    }
}
