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
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationWorkLocationRepository;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.recruitment.domain.Matching;
import com.becareful.becarefulserver.domain.recruitment.domain.MatchingStatus;
import com.becareful.becarefulserver.domain.recruitment.domain.Recruitment;
import com.becareful.becarefulserver.domain.recruitment.domain.vo.MatchingInfo;
import com.becareful.becarefulserver.domain.recruitment.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.recruitment.dto.request.RecruitmentMediateRequest;
import com.becareful.becarefulserver.domain.recruitment.dto.response.MyRecruitmentDetailResponse;
import com.becareful.becarefulserver.domain.recruitment.dto.response.MyRecruitmentResponse;
import com.becareful.becarefulserver.domain.recruitment.dto.response.RecruitmentDetailResponse;
import com.becareful.becarefulserver.domain.recruitment.dto.response.RecruitmentResponse;
import com.becareful.becarefulserver.domain.recruitment.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.recruitment.repository.RecruitmentRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.ResidentialAddress;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.domain.work_location.domain.WorkLocation;
import com.becareful.becarefulserver.global.exception.exception.RecruitmentException;
import com.becareful.becarefulserver.global.util.AuthUtil;

import java.time.DayOfWeek;
import java.util.EnumSet;
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
                .map(workApplication -> matchingRepository.findAllByWorkApplication(workApplication).stream()
                        .map(RecruitmentResponse::from)
                        .sorted(response -> response.matchRate())
                        .toList())
                .orElse(null);
    }

    public RecruitmentDetailResponse getRecruitmentDetail(Long recruitmentId) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));

        // TODO : recruit 매칭 적합도 및 태그 부여 판단
        return RecruitmentDetailResponse.from(recruitment, false, false, 98);
    }

    public List<MyRecruitmentResponse> getMyRecruitment(MatchingStatus matchingStatus) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        return workApplicationRepository.findByCaregiver(caregiver)
                .map(workApplication -> matchingRepository.findByWorkApplicationAndMatchingStatus(workApplication, matchingStatus).stream()
                        .map(matching -> MyRecruitmentResponse.of(matching.getRecruitment(), matching.getMatchingStatus()))
                        .toList())
                .orElse(List.of());
    }

    public MyRecruitmentDetailResponse getMyRecruitmentDetail(Long recruitmentId) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));
        WorkApplication workApplication = workApplicationRepository.findByCaregiver(caregiver)
                .orElseThrow(() -> new RecruitmentException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));

        Matching matching = matchingRepository.findByWorkApplicationAndRecruitment(workApplication, recruitment)
                .orElseThrow(() -> new RecruitmentException(MATCHING_NOT_EXISTS));

        // TODO : recruit 매칭 적합도 및 태그 부여 판단
        return MyRecruitmentDetailResponse.of(
                recruitment, false, false, 98, matching.getApplicationDate());
    }

    @Transactional
    public void mediateMatching(Long recruitmentId, RecruitmentMediateRequest request) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new RecruitmentException(RECRUITMENT_NOT_EXISTS));
        WorkApplication workApplication = workApplicationRepository.findByCaregiver(caregiver)
                .orElseThrow(() -> new RecruitmentException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));

        Matching matching = matchingRepository.findByWorkApplicationAndRecruitment(workApplication, recruitment)
                .orElseThrow(() -> new RecruitmentException(MATCHING_NOT_EXISTS));

        matching.mediate(request);
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

        // TODO 이미 근무 중인 데이터와 일정이 겹치는지 체크
        matchingWith(recruitment);

        return recruitment.getId();
    }

    private void matchingWith(Recruitment recruitment) {
        workApplicationRepository.findAllActiveWorkApplication().stream()
                .map(application -> {
                    List<WorkLocation> locations = workApplicationWorkLocationRepository.findAllByWorkApplication(application).stream()
                            .map(WorkApplicationWorkLocation::getWorkLocation)
                            .toList();

                    MatchingInfo caregiverMatchingInfo = calculateMatchingRate(recruitment, application, locations, true);
                    MatchingInfo socialworkerMatchingInfo = calculateMatchingRate(recruitment, application, locations, false);

                    System.out.println(Matching.create(recruitment, application, caregiverMatchingInfo, socialworkerMatchingInfo));

                    return Matching.create(recruitment, application, caregiverMatchingInfo, socialworkerMatchingInfo);
                })
                .filter((matching -> isMatchedWithSocialWorker(matching.getSocialWorkerMatchingInfo())))
                .forEach(matchingRepository::save);
    }

    private MatchingInfo calculateMatchingRate(Recruitment recruitment, WorkApplication workApplication, List<WorkLocation> locations, boolean isForCaregiver) {
        Double workTimeMatchingRate = calculateWorkTimeMatchingRate(recruitment.getWorkTimes(), workApplication.getWorkTimes(), isForCaregiver);
        Double workDayMatchingRate = calculateDayMatchingRate(recruitment.getWorkDays(), workApplication.getWorkDays(), isForCaregiver);
        Double workSalaryMatchingRate = calculateWorkSalaryMatchingRate(recruitment.getWorkSalaryAmount(), workApplication.getWorkSalaryAmount(), isForCaregiver);
        Double workLocationMatchingRate = calculateWorkLocationMatchingRate(recruitment.getResidentialAddress(), locations);
        Double workCareTypeMatchingRate = calculateWorkCareTypeMatchingRate(recruitment.getCareTypes(), workApplication.getWorkCareTypes(), isForCaregiver);

        return MatchingInfo.builder()
                .workTimeMatchingRate(workTimeMatchingRate)
                .workDayMatchingRate(workDayMatchingRate)
                .workSalaryMatchingRate(workSalaryMatchingRate)
                .workLocationMatchingRate(workLocationMatchingRate)
                .workCareTypeMatchingRate(workCareTypeMatchingRate)
                .workSalaryDifference(workApplication.getWorkSalaryAmount() - recruitment.getWorkSalaryAmount())
                .build();
    }

    private Double calculateWorkCareTypeMatchingRate(EnumSet<CareType> recruitmentCareTypes, EnumSet<CareType> applicationCareTypes, boolean isForCaregiver) {
        EnumSet<CareType> intersection = EnumSet.copyOf(recruitmentCareTypes);
        intersection.retainAll(applicationCareTypes);

        if (isForCaregiver) {
            return ((double) intersection.size()) / applicationCareTypes.size() * 100;
        }
        return ((double) intersection.size() / recruitmentCareTypes.size()) * 100;
    }

    private Double calculateWorkLocationMatchingRate(ResidentialAddress residentialAddress, List<WorkLocation> locations) {
        return locations.stream()
                .map(location -> location.calculateMatchingRate(residentialAddress))
                .max(Double::compareTo)
                .orElse(0.0);
    }

    private Double calculateWorkSalaryMatchingRate(int recruitmentSalaryAmount, int applicationSalaryAmount, boolean isForCaregiver) {
        // 급여 타입은 시급만 고려
        // 매칭 대상 비율은 50 ~ 100% 사이, 그 이외 범위는 매칭 안됨
        // 요양 보호사 계산식 = (이 값이 0 ~ 1000 인 것만 매칭, 요양보호사는 이 값 차이가 큰 순으로 정렬, 사회복지사는 이 값이 작을수록 적합도 상승 (2000 - 이 값) / 2000 으로 계산)
        int workSalaryDifference = applicationSalaryAmount - recruitmentSalaryAmount;

        if (workSalaryDifference < 0 || workSalaryDifference > 1000) {
            return 0.0;
        }

        // 범위만 체크하는 방식 (요양보호사 관점은 없음)
        return 100.0;

        // 비율 계산 방식
//        if (isForCaregiver) {
//            return (double) (workSalaryDifference + 1000) / 2000;
//        }
//
//        return (double) (2000 - workSalaryDifference) / 2000;
    }

    private Double calculateWorkTimeMatchingRate(EnumSet<WorkTime> recruitmentTimes, EnumSet<WorkTime> applyTimes, boolean isForCaregiver) {
        EnumSet<WorkTime> intersection = EnumSet.copyOf(recruitmentTimes);
        intersection.retainAll(applyTimes);

        if (isForCaregiver) {
            return ((double) intersection.size()) / applyTimes.size() * 100;
        }
        return ((double) intersection.size() / recruitmentTimes.size()) * 100;
    }

    private Double calculateDayMatchingRate(EnumSet<DayOfWeek> recruitmentDays, EnumSet<DayOfWeek> applyDays, boolean isForCaregiver) {
        EnumSet<DayOfWeek> intersection = EnumSet.copyOf(recruitmentDays);
        intersection.retainAll(applyDays);

        if (isForCaregiver) {
            return ((double) intersection.size() / applyDays.size()) * 100;
        }
        return ((double) intersection.size() / recruitmentDays.size()) * 100;
    }

    private boolean isMatchedWithSocialWorker(MatchingInfo socialWorkerMatchingInfo) {
        if (socialWorkerMatchingInfo.getWorkTimeMatchingRate() == 0.0) {
            return false;
        }

        if (isNotMatchedWithDaysOfWeek(socialWorkerMatchingInfo)) {
            return false;
        }

        if (socialWorkerMatchingInfo.getWorkSalaryMatchingRate() < 50.0) {
            return false;
        }

        if (socialWorkerMatchingInfo.getWorkLocationMatchingRate() < 50.0) {
            return false;
        }

        if (socialWorkerMatchingInfo.getWorkCareTypeMatchingRate() < 50.0) {
            return false;
        }

        return true;
    }

    private boolean isNotMatchedWithDaysOfWeek(MatchingInfo matchingInfo) {
        if (matchingInfo.getWorkDayMatchingRate() == 0.0) {
            return true;
        }

        if (matchingInfo.getWorkDayMatchingRate() >= 50.0) {
            return false;
        }

        if (matchingInfo.getWorkLocationMatchingRate() >= 100.0 &&
            matchingInfo.getWorkSalaryMatchingRate() >= 100 &&
                matchingInfo.getWorkCareTypeMatchingRate() >= 100
        ) {
            return false;
        }

        return true;
    }
}
