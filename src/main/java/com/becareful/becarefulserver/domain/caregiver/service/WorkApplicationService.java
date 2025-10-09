package com.becareful.becarefulserver.domain.caregiver.service;

import static com.becareful.becarefulserver.domain.matching.domain.MatchingStatus.미지원;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.CAREGIVER_WORK_APPLICATION_NOT_EXISTS;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.dto.WorkApplicationDto;
import com.becareful.becarefulserver.domain.caregiver.dto.request.WorkApplicationCreateOrUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.dto.response.*;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationRepository;
import com.becareful.becarefulserver.domain.chat.repository.CaregiverChatReadStatusRepository;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.service.MatchingDomainService;
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
public class WorkApplicationService {

    private final AuthUtil authUtil;
    private final MatchingDomainService matchingDomainService;
    private final WorkApplicationRepository workApplicationRepository;
    private final MatchingRepository matchingRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final CaregiverChatReadStatusRepository caregiverChatReadStatusRepository;

    @Transactional(readOnly = true)
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
    public void createOrUpdateWorkApplication(WorkApplicationCreateOrUpdateRequest request) {
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

        matchingRepository.deleteAllByApplicationAndMatchingStatus(application, 미지원);

        application.inactivate();
    }

    private void matchingWith(WorkApplication application) {
        matchingRepository.deleteAllByApplicationAndMatchingStatus(application, 미지원);
        recruitmentRepository.findAllByIsRecruiting().forEach(recruitment -> {
            matchingDomainService.createMatching(recruitment, application).ifPresent(matchingRepository::save);
        });
    }
}
