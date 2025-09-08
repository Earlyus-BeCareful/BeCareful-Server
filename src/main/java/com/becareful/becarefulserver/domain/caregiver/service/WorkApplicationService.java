package com.becareful.becarefulserver.domain.caregiver.service;

import static com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus.미지원;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.CAREGIVER_WORK_APPLICATION_NOT_EXISTS;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplicationWorkLocation;
import com.becareful.becarefulserver.domain.caregiver.dto.WorkApplicationDto;
import com.becareful.becarefulserver.domain.caregiver.dto.request.WorkApplicationUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.dto.response.*;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationWorkLocationRepository;
import com.becareful.becarefulserver.domain.chat.service.*;
import com.becareful.becarefulserver.domain.common.domain.vo.Location;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.matching.repository.RecruitmentRepository;
import com.becareful.becarefulserver.domain.work_location.domain.WorkLocation;
import com.becareful.becarefulserver.domain.work_location.dto.request.WorkLocationDto;
import com.becareful.becarefulserver.domain.work_location.repository.WorkLocationRepository;
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
    private final WorkLocationRepository workLocationRepository;
    private final WorkApplicationWorkLocationRepository workApplicationWorkLocationRepository;
    private final MatchingRepository matchingRepository;
    private final AuthUtil authUtil;
    private final RecruitmentRepository recruitmentRepository;
    private final CaregiverChatService caregiverChatService;

    public CaregiverMyWorkApplicationPageResponse getMyWorkApplicationPageInfo() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();

        boolean hasNewChat = caregiverChatService.checkNewChat(caregiver);
        WorkApplicationDto workApplicationDto = workApplicationRepository
                .findByCaregiver(caregiver)
                .map(workApplication -> {
                    List<WorkLocationDto> locations =
                            workApplicationWorkLocationRepository.findAllByWorkApplication(workApplication).stream()
                                    .map(data -> WorkLocationDto.from(data.getWorkLocation()))
                                    .toList();

                    return WorkApplicationDto.of(locations, workApplication);
                })
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
                            workApplicationWorkLocationRepository.deleteAllByWorkApplication(application);
                            saveWorkLocations(request.workLocations(), application);
                            matchingWith(application);
                        },
                        () -> {
                            WorkApplication application = WorkApplication.create(request, caregiver);
                            workApplicationRepository.save(application);
                            saveWorkLocations(request.workLocations(), application);
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

    private void saveWorkLocations(List<WorkLocationDto> workLocations, WorkApplication workApplication) {
        for (WorkLocationDto workLocation : workLocations) {
            WorkLocation location = workLocationRepository
                    .findByLocation(
                            Location.of(workLocation.siDo(), workLocation.siGuGun(), workLocation.dongEupMyeon()))
                    .orElseGet(() -> workLocationRepository.save(WorkLocation.from(workLocation)));

            var data = WorkApplicationWorkLocation.of(workApplication, location);
            workApplicationWorkLocationRepository.save(data);
        }
    }

    private void matchingWith(WorkApplication application) {
        List<Matching> matchingList =
                matchingRepository.findAllByCaregiverAndApplicationStatus(application.getCaregiver(), 미지원);
        matchingRepository.deleteAll(matchingList);
        List<Location> locations = workApplicationWorkLocationRepository.findAllByWorkApplication(application).stream()
                .map(WorkApplicationWorkLocation::getLocation)
                .toList();
        recruitmentRepository.findAll().stream()
                .filter(Recruitment::isRecruiting)
                .map(recruitment -> {
                    return Matching.create(recruitment, application, locations);
                })
                // TODO : 매칭 알고리즘 해제하기.
                // .filter((matching -> isMatchedWithSocialWorker(matching.getSocialWorkerMatchingInfo())))
                .forEach(matchingRepository::save);
    }
}
