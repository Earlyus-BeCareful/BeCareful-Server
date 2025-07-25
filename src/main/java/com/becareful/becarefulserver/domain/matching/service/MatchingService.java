package com.becareful.becarefulserver.domain.matching.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.CareerDetail;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplicationWorkLocation;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.caregiver.repository.CareerDetailRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.CareerRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationWorkLocationRepository;
import com.becareful.becarefulserver.domain.common.vo.Location;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultInfo;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.matching.dto.MatchedCaregiverDto;
import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentMediateRequest;
import com.becareful.becarefulserver.domain.matching.dto.response.*;
import com.becareful.becarefulserver.domain.matching.dto.response.MatchingCaregiverDetailResponse;
import com.becareful.becarefulserver.domain.matching.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.matching.repository.RecruitmentRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.domain.work_location.dto.request.WorkLocationDto;
import com.becareful.becarefulserver.domain.work_location.repository.WorkLocationRepository;
import com.becareful.becarefulserver.global.exception.exception.CaregiverException;
import com.becareful.becarefulserver.global.exception.exception.RecruitmentException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final WorkLocationRepository workLocationRepository;

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

    public List<CaregiverMatchingRecruitmentResponse> getCaregiverMatchingRecruitmentList() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        return workApplicationRepository
                .findByCaregiver(caregiver)
                .map(workApplication -> matchingRepository.findAllByWorkApplication(workApplication).stream()
                        .map(CaregiverMatchingRecruitmentResponse::from)
                        .toList())
                .orElse(null);
    }

    public RecruitmentDetailResponse getRecruitmentDetail(Long recruitmentId) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));

        // TODO : recruit 매칭 적합도 및 태그 부여 판단
        return RecruitmentDetailResponse.from(recruitment, false, false, 98);
    }

    public List<CaregiverAppliedMatchingRecruitmentResponse> getMyRecruitment(
            MatchingApplicationStatus matchingApplicationStatus) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        return workApplicationRepository
                .findByCaregiver(caregiver)
                .map(
                        workApplication -> matchingRepository
                                .findByWorkApplicationAndMatchingApplicationStatus(
                                        workApplication, matchingApplicationStatus)
                                .stream()
                                .map(CaregiverAppliedMatchingRecruitmentResponse::from)
                                .toList())
                .orElse(List.of());
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

        // TODO : recruit 매칭 적합도 및 태그 부여 판단
        return CaregiverAppliedMatchingDetailResponse.of(recruitment, false, false, 98, matching.getApplicationDate());
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

    public List<MatchingStatusSimpleResponse> getMatchingList() {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();
        List<Recruitment> recruitments = recruitmentRepository.findAllByInstitutionId(
                socialworker.getNursingInstitution().getId());

        return recruitments.stream()
                .filter(Recruitment::isRecruiting)
                .map(recruitment -> {
                    int notAppliedMatchingCount = matchingRepository.countByRecruitmentAndMatchingApplicationStatus(
                            recruitment, MatchingApplicationStatus.미지원); // 거절 제거 할래말래
                    int appliedMatchingCount = matchingRepository.countByRecruitmentAndMatchingApplicationStatus(
                            recruitment, MatchingApplicationStatus.지원검토중);

                    return MatchingStatusSimpleResponse.of(recruitment, notAppliedMatchingCount, appliedMatchingCount);
                })
                .toList();
    }

    // 매칭 상세 - 공고 상세 페이지
    public MatchingStatusDetailResponse getMatchingDetail(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));
        List<Matching> matchings = matchingRepository.findByRecruitment(recruitment);

        List<MatchingCaregiverSimpleResponse> unAppliedCaregivers = new ArrayList<>();
        List<MatchingCaregiverSimpleResponse> appliedCaregivers = new ArrayList<>();

        matchings.forEach(matching -> {
            Caregiver caregiver = matching.getWorkApplication().getCaregiver();
            Career career = careerRepository
                    .findByCaregiver(caregiver)
                    .orElseThrow(() -> new CaregiverException(CAREGIVER_CAREER_NOT_EXISTS));

            MatchedCaregiverDto caregiverInfo = MatchedCaregiverDto.of(caregiver, career);
            MatchingResultStatus matchingResult =
                    matching.getSocialWorkerMatchingResultInfo().judgeMatchingResultStatus();

            var matchedCaregiverInfo = MatchingCaregiverSimpleResponse.of(caregiverInfo, matchingResult);

            if (matching.getMatchingApplicationStatus().equals(MatchingApplicationStatus.지원검토중)) {
                appliedCaregivers.add(matchedCaregiverInfo);
            } else if (matching.getMatchingApplicationStatus().equals(MatchingApplicationStatus.미지원)) {
                unAppliedCaregivers.add(matchedCaregiverInfo);
            }
        });

        return MatchingStatusDetailResponse.of(recruitment, unAppliedCaregivers, appliedCaregivers);
    }

    private void matchingWith(Recruitment recruitment) {
        workApplicationRepository.findAllActiveWorkApplication().stream()
                .map(application -> {
                    List<Location> locations =
                            workApplicationWorkLocationRepository.findAllByWorkApplication(application).stream()
                                    .map(WorkApplicationWorkLocation::getLocation)
                                    .toList();

                    MatchingResultInfo caregiverMatchingResultInfo =
                            calculateMatchingRate(recruitment, application, locations, true);
                    MatchingResultInfo socialworkerMatchingResultInfo =
                            calculateMatchingRate(recruitment, application, locations, false);

                    return Matching.create(
                            recruitment, application, caregiverMatchingResultInfo, socialworkerMatchingResultInfo);
                })
                // TODO : 매칭 알고리즘 해제하기.
                // .filter((matching -> isMatchedWithSocialWorker(matching.getSocialWorkerMatchingInfo())))
                .forEach(matchingRepository::save);
    }

    /**
     * @param recruitment       - 사회복지사가 등록한 공고
     * @param workApplication   - 요양보호사가 등록한 지원서
     * @param locations         - 지원서에 등록된 희망 근무 장소
     * @param isForCaregiver    - 요양보호사 기준 적합도인지, 사회복지사 기준 적합도인지 기준
     * @return                  - MatchingInfo
     */
    private MatchingResultInfo calculateMatchingRate(
            Recruitment recruitment,
            WorkApplication workApplication,
            List<Location> locations,
            boolean isForCaregiver) {
        boolean workLocationMatchingRate = isWorkLocationMatched(recruitment.getResidentialLocation(), locations);
        Double workDayMatchingRate =
                calculateDayMatchingRate(recruitment.getWorkDays(), workApplication.getWorkDays(), isForCaregiver);
        boolean workTimeMatchingRate =
                isWorkTimeMatched(recruitment.getWorkTimes(), workApplication.getWorkTimes(), isForCaregiver);

        return MatchingResultInfo.create(workLocationMatchingRate, workDayMatchingRate, workTimeMatchingRate);
    }

    private boolean isWorkLocationMatched(Location residentialLocation, List<Location> workableLocations) {
        return workableLocations.contains(residentialLocation);
    }

    private Double calculateDayMatchingRate(
            EnumSet<DayOfWeek> recruitmentDays, EnumSet<DayOfWeek> applyDays, boolean isForCaregiver) {
        EnumSet<DayOfWeek> intersection = EnumSet.copyOf(recruitmentDays);
        intersection.retainAll(applyDays);

        if (isForCaregiver) {
            return ((double) intersection.size() / applyDays.size()) * 100;
        }
        return ((double) intersection.size() / recruitmentDays.size()) * 100;
    }

    private boolean isWorkTimeMatched(
            EnumSet<WorkTime> recruitmentTimes, EnumSet<WorkTime> applyTimes, boolean isForCaregiver) {
        EnumSet<WorkTime> intersection = EnumSet.copyOf(recruitmentTimes);
        intersection.retainAll(applyTimes);

        if (isForCaregiver) {
            return ((double) intersection.size()) / applyTimes.size() * 100 == 100;
        }
        return ((double) intersection.size() / recruitmentTimes.size()) * 100 != 100;
    }
}
