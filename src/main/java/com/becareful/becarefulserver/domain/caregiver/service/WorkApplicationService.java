package com.becareful.becarefulserver.domain.caregiver.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.CAREGIVER_WORK_APPLICATION_NOT_EXISTS;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplicationWorkLocation;
import com.becareful.becarefulserver.domain.caregiver.dto.request.WorkApplicationUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.dto.response.WorkApplicationResponse;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationWorkLocationRepository;
import com.becareful.becarefulserver.domain.work_location.domain.WorkLocation;
import com.becareful.becarefulserver.domain.work_location.dto.request.WorkLocationDto;
import com.becareful.becarefulserver.domain.work_location.repository.WorkLocationRepository;
import com.becareful.becarefulserver.global.exception.exception.CaregiverException;
import com.becareful.becarefulserver.global.util.AuthUtil;

import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkApplicationService {

    private final WorkApplicationRepository workApplicationRepository;
    private final WorkLocationRepository workLocationRepository;
    private final WorkApplicationWorkLocationRepository workApplicationWorkLocationRepository;
    private final AuthUtil authUtil;

    public WorkApplicationResponse getWorkApplication() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();

        return workApplicationRepository.findByCaregiver(caregiver)
                .map(workApplication -> {
                    List<WorkLocationDto> locations = workApplicationWorkLocationRepository.findAllByWorkApplication(
                                    workApplication)
                            .stream().map(data -> WorkLocationDto.from(data.getWorkLocation())).toList();

                    return WorkApplicationResponse.of(locations, workApplication);
                })
                .orElse(WorkApplicationResponse.empty());
    }

    @Transactional
    public void updateWorkApplication(WorkApplicationUpdateRequest request) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        workApplicationRepository.findByCaregiver(caregiver)
                .ifPresentOrElse(
                        workApplication -> {
                            workApplication.updateWorkApplication(request);
                            workApplicationWorkLocationRepository.deleteAllByWorkApplication(workApplication);
                            saveWorkLocations(request.workLocations(), workApplication);
                        },
                        () -> {
                            WorkApplication application = WorkApplication.create(request, caregiver);
                            workApplicationRepository.save(application);
                            saveWorkLocations(request.workLocations(), application);
                        }
                );
    }

    @Transactional
    public void updateWorkApplicationActive() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        WorkApplication application = workApplicationRepository.findByCaregiver(caregiver)
                .orElseThrow(() -> new CaregiverException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));

        application.activate();
    }

    @Transactional
    public void updateWorkApplicationInactive() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        WorkApplication application = workApplicationRepository.findByCaregiver(caregiver)
                .orElseThrow(() -> new CaregiverException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));

        application.inactivate();
    }



    private void saveWorkLocations(List<WorkLocationDto> workLocations, WorkApplication workApplication) {
        for (WorkLocationDto workLocation : workLocations) {
            WorkLocation location = workLocationRepository.findBySiDoAndSiGuGunAndEupMyeonDong(
                    workLocation.siDo(), workLocation.siGuGun(), workLocation.dongEupMyeon())
                    .orElseGet(() -> workLocationRepository.save(WorkLocation.from(workLocation)));

            var data = WorkApplicationWorkLocation.of(workApplication, location);
            workApplicationWorkLocationRepository.save(data);
        }
    }
}
