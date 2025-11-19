package com.becareful.becarefulserver.domain.caregiver.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.CAREGIVER_WORK_APPLICATION_NOT_EXISTS;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.dto.WorkApplicationDto;
import com.becareful.becarefulserver.domain.caregiver.dto.request.WorkApplicationCreateOrUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.dto.response.*;
import com.becareful.becarefulserver.domain.caregiver.repository.CareerRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationRepository;
import com.becareful.becarefulserver.domain.chat.repository.CaregiverChatReadStatusRepository;
import com.becareful.becarefulserver.global.exception.exception.CaregiverException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkApplicationService {

    private final AuthUtil authUtil;
    private final WorkApplicationRepository workApplicationRepository;
    private final CaregiverChatReadStatusRepository caregiverChatReadStatusRepository;
    private final CareerRepository careerRepository;

    @Transactional(readOnly = true)
    public CaregiverMyWorkApplicationPageResponse getMyWorkApplicationPageInfo() {
        Caregiver loggedInCaregiver = authUtil.getLoggedInCaregiver();

        boolean hasNewChat = caregiverChatReadStatusRepository.existsUnreadChat(loggedInCaregiver);
        boolean hasCareer = careerRepository.existsByCaregiver(loggedInCaregiver);
        WorkApplicationDto workApplicationDto = workApplicationRepository
                .findByCaregiver(loggedInCaregiver)
                .map(WorkApplicationDto::from)
                .orElse(null);

        return CaregiverMyWorkApplicationPageResponse.of(hasNewChat, hasCareer, workApplicationDto);
    }

    @Transactional
    public void createOrUpdateWorkApplication(WorkApplicationCreateOrUpdateRequest request) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        workApplicationRepository
                .findByCaregiver(caregiver)
                .ifPresentOrElse(
                        application -> {
                            application.updateWorkApplication(request);
                        },
                        () -> {
                            WorkApplication application = WorkApplication.create(request, caregiver);
                            workApplicationRepository.save(application);
                        });
    }

    @Transactional
    public void updateWorkApplicationActive() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        WorkApplication application = workApplicationRepository
                .findByCaregiver(caregiver)
                .orElseThrow(() -> new CaregiverException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));

        application.activate();
    }

    @Transactional
    public void updateWorkApplicationInactive() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        WorkApplication application = workApplicationRepository
                .findByCaregiver(caregiver)
                .orElseThrow(() -> new CaregiverException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));

        application.inactivate();
    }
}
