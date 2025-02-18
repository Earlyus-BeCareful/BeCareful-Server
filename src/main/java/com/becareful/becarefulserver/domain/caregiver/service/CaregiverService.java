package com.becareful.becarefulserver.domain.caregiver.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.CAREGIVER_ALREADY_EXISTS;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.CAREGIVER_FAILED_TO_UPLOAD_PROFILE_IMAGE;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.CAREGIVER_REQUIRED_AGREEMENT;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.vo.CaregiverInfo;
import com.becareful.becarefulserver.domain.caregiver.dto.request.CaregiverCreateRequest;
import com.becareful.becarefulserver.domain.caregiver.dto.response.CaregiverHomeResponse;
import com.becareful.becarefulserver.domain.caregiver.dto.response.CaregiverMyPageHomeResponse;
import com.becareful.becarefulserver.domain.caregiver.dto.response.CaregiverProfileUploadResponse;
import com.becareful.becarefulserver.domain.caregiver.repository.CareerRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationWorkLocationRepository;
import com.becareful.becarefulserver.domain.recruitment.domain.MatchingStatus;
import com.becareful.becarefulserver.domain.recruitment.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.work_location.dto.request.WorkLocationDto;
import com.becareful.becarefulserver.global.exception.exception.CaregiverException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import com.becareful.becarefulserver.global.util.FileUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaregiverService {

    private final CaregiverRepository caregiverRepository;
    private final CareerRepository careerRepository;
    private final WorkApplicationRepository workApplicationRepository;
    private final WorkApplicationWorkLocationRepository workApplicationWorkLocationRepository;
    private final MatchingRepository matchingRepository;
    private final FileUtil fileUtil;
    private final AuthUtil authUtil;

    public CaregiverHomeResponse getHomeData() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        return workApplicationRepository.findByCaregiver(caregiver)
                .map(workApplication -> {
                    Integer recruitmentCount = matchingRepository.countByWorkApplicationAndMatchingStatus(workApplication, MatchingStatus.미지원);
                    Integer applicationCount = matchingRepository.countByWorkApplicationAndMatchingStatus(workApplication, MatchingStatus.지원);
                    return CaregiverHomeResponse.of(caregiver, applicationCount, recruitmentCount, false);})
                .orElse(CaregiverHomeResponse.createNotHavingWorkApplication(caregiver));
    }

    public CaregiverMyPageHomeResponse getMyPageHomeData() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        Career career = careerRepository.findByCaregiver(caregiver).orElse(null);
        WorkApplication workApplication = workApplicationRepository.findByCaregiver(caregiver).orElse(null);
        List<WorkLocationDto> locations = workApplicationWorkLocationRepository.findAllByWorkApplication(
                        workApplication)
                .stream().map(data -> WorkLocationDto.from(data.getWorkLocation())).toList();
        return CaregiverMyPageHomeResponse.of(caregiver, career, workApplication, locations);
    }

    @Transactional
    public Long saveCaregiver(CaregiverCreateRequest request) {
        validateEssentialAgreement(request.isAgreedToTerms(),
                request.isAgreedToCollectPersonalInfo());

        if (caregiverRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new CaregiverException(CAREGIVER_ALREADY_EXISTS);
        }

        CaregiverInfo caregiverInfo = CaregiverInfo.builder()
                .isHavingCar(request.isHavingCar())
                .isCompleteDementiaEducation(request.isCompleteDementiaEducation())
                .caregiverCertificate(request.caregiverCertificate())
                .socialWorkerCertificate(request.socialWorkerCertificate())
                .nursingCareCertificate(request.nursingCareCertificate())
                .build();

        Caregiver caregiver = Caregiver.create(
                request.name(), request.birthDate(), request.phoneNumber(),
                getEncodedPassword(request.password()), request.gender(), request.streetAddress(),
                request.detailAddress(), caregiverInfo, request.isAgreedToReceiveMarketingInfo(),
                request.profileImageUrl());

        caregiverRepository.save(caregiver);
        return caregiver.getId();
    }

    @Transactional
    public CaregiverProfileUploadResponse uploadProfileImage(MultipartFile file,
            String phoneNumber) {
        try {
            String fileName = generateProfileImageFileName(phoneNumber);
            String profileImageUrl = fileUtil.upload(file, fileName);
            return new CaregiverProfileUploadResponse(profileImageUrl);
        } catch (IOException e) {
            throw new CaregiverException(CAREGIVER_FAILED_TO_UPLOAD_PROFILE_IMAGE);
        }
    }

    private void validateEssentialAgreement(boolean isAgreedToTerms,
            boolean isAgreedToCollectPersonalInfo) {
        if (isAgreedToTerms && isAgreedToCollectPersonalInfo) {
            return;
        }

        throw new CaregiverException(CAREGIVER_REQUIRED_AGREEMENT);
    }

    private String getEncodedPassword(String rawPassword) {
        var passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(rawPassword);
    }

    private String generateProfileImageFileName(String phoneNumber) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(phoneNumber.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new CaregiverException(CAREGIVER_FAILED_TO_UPLOAD_PROFILE_IMAGE);
        }
    }
}
