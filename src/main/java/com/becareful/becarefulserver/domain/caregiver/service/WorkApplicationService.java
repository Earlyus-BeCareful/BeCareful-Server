package com.becareful.becarefulserver.domain.caregiver.service;

import static com.becareful.becarefulserver.domain.matching.domain.MatchingStatus.미지원;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.CAREGIVER_WORK_APPLICATION_NOT_EXISTS;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.dto.WorkApplicationDto;
import com.becareful.becarefulserver.domain.caregiver.dto.request.WorkApplicationUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.dto.response.*;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationRepository;
import com.becareful.becarefulserver.domain.chat.repository.CaregiverChatReadStatusRepository;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.matching.repository.RecruitmentRepository;
import com.becareful.becarefulserver.global.exception.exception.CaregiverException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkApplicationService {

    private final WorkApplicationRepository workApplicationRepository;
    private final MatchingRepository matchingRepository;
    private final AuthUtil authUtil;
    private final RecruitmentRepository recruitmentRepository;
    private final CaregiverChatReadStatusRepository caregiverChatReadStatusRepository;

    public CaregiverMyWorkApplicationPageResponse getMyWorkApplicationPageInfo() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();

        boolean hasNewChat = caregiverChatReadStatusRepository.existsUnreadContract(caregiver);
        WorkApplicationDto workApplicationDto = workApplicationRepository
                .findByCaregiver(caregiver)
                .map(WorkApplicationDto::from)
                .orElse(null);

        return CaregiverMyWorkApplicationPageResponse.of(hasNewChat, workApplicationDto);
    }

    @Transactional
    public void updateWorkApplication(WorkApplicationUpdateRequest request) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        workApplicationRepository
                .findByCaregiver(caregiver)
                .ifPresentOrElse(
                        application -> {
                            application.updateWorkApplication(request);
                            matchingWith(application);
                        },
                        () -> {
                            WorkApplication application = WorkApplication.create(request, caregiver);
                            workApplicationRepository.save(application);
                            matchingWith(application);
                        });
    }

    @Transactional
    public void updateWorkApplicationActive() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        WorkApplication application = workApplicationRepository
                .findByCaregiver(caregiver)
                .orElseThrow(() -> new CaregiverException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));

        application.activate();
        matchingWith(application);
    }

    @Transactional
    public void updateWorkApplicationInactive() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        WorkApplication application = workApplicationRepository
                .findByCaregiver(caregiver)
                .orElseThrow(() -> new CaregiverException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));

        List<Matching> matchingList =
                matchingRepository.findAllByCaregiverAndApplicationStatus(application.getCaregiver(), 미지원);
        matchingRepository.deleteAll(matchingList);

        application.inactivate();
    }

    private void matchingWith(WorkApplication application) {
        List<Matching> matchingList =
                matchingRepository.findAllByCaregiverAndApplicationStatus(application.getCaregiver(), 미지원);
        matchingRepository.deleteAll(matchingList);
        recruitmentRepository.findAll().stream()
                .filter(r -> r.getRecruitmentStatus().isRecruiting())
                .map(recruitment -> Matching.create(recruitment, application))
                // TODO : 매칭 알고리즘 해제하기.
                // .filter((matching -> isMatchedWithSocialWorker(matching.getSocialWorkerMatchingInfo())))
                .forEach(matchingRepository::save);
    }
}
