package com.becareful.becarefulserver.domain.caregiver.service;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.CareerDetail;
import com.becareful.becarefulserver.domain.caregiver.domain.CareerType;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.dto.CareerDto;
import com.becareful.becarefulserver.domain.caregiver.dto.request.CareerCreateOrUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.repository.CareerDetailRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.CareerRepository;
import com.becareful.becarefulserver.global.util.AuthUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareerService {

    private final CareerRepository careerRepository;
    private final CareerDetailRepository careerDetailRepository;
    private final AuthUtil authUtil;

    public CareerDto getCareer() {
        Caregiver loggedInCaregiver = authUtil.getLoggedInCaregiver();
        return careerRepository
                .findByCaregiver(loggedInCaregiver)
                .map(career -> {
                    List<CareerDetail> careerDetails = careerDetailRepository.findAllByCareer(career);
                    return CareerDto.of(career, careerDetails);
                })
                .orElseGet(CareerDto::empty);
    }

    @Transactional
    public void createOrUpdateCareer(CareerCreateOrUpdateRequest request) {
        Caregiver loggedInCaregiver = authUtil.getLoggedInCaregiver();
        careerRepository
                .findByCaregiver(loggedInCaregiver)
                .ifPresentOrElse(
                        career -> updateCareerAndCareerDetail(request, career),
                        () -> createCareerAndCareerDetail(request, loggedInCaregiver));
    }

    private void createCareerAndCareerDetail(CareerCreateOrUpdateRequest request, Caregiver caregiver) {
        Career career = Career.create(request.title(), request.careerType(), request.introduce(), caregiver);
        careerRepository.save(career);

        if (career.hasCareer()) {
            request.careerDetails().forEach(careerDetailUpdateRequest -> {
                CareerDetail careerDetail = CareerDetail.create(
                        careerDetailUpdateRequest.workInstitution(), careerDetailUpdateRequest.workYear(), career);
                careerDetailRepository.save(careerDetail);
            });
        }
    }

    private void updateCareerAndCareerDetail(CareerCreateOrUpdateRequest request, Career career) {
        career.updateCareer(request.title(), request.careerType(), request.introduce());

        careerDetailRepository.deleteAllByCareer(career);

        if (request.careerType().equals(CareerType.경력)) {
            request.careerDetails().forEach(careerDetailUpdateRequest -> {
                CareerDetail careerDetail = CareerDetail.create(
                        careerDetailUpdateRequest.workInstitution(), careerDetailUpdateRequest.workYear(), career);
                careerDetailRepository.save(careerDetail);
            });
        }
    }
}
