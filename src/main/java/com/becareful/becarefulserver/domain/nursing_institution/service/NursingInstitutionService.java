package com.becareful.becarefulserver.domain.nursing_institution.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.dto.request.NursingInstitutionCreateRequest;
import com.becareful.becarefulserver.domain.nursing_institution.dto.response.NursingInstitutionProfileUploadResponse;
import com.becareful.becarefulserver.domain.nursing_institution.dto.response.NursingInstitutionSearchResponse;
import com.becareful.becarefulserver.domain.nursing_institution.repository.NursingInstitutionRepository;
import com.becareful.becarefulserver.domain.nursing_institution.vo.FacilityType;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.global.exception.exception.NursingInstitutionException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import com.becareful.becarefulserver.global.util.FileUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.EnumSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class NursingInstitutionService {
    private final NursingInstitutionRepository nursingInstitutionRepository;
    private final FileUtil fileUtil;
    private final AuthUtil authUtil;

    @Transactional
    public boolean existsById() {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();
        Long institutionId = socialworker.getNursingInstitution().getId();
        return nursingInstitutionRepository.existsById(institutionId);
    }

    @Transactional(readOnly = true)
    public Boolean existsByNameAndCode(String nursingInstitutionCode) {
        if (nursingInstitutionCode == null) {
            throw new NursingInstitutionException(NURSING_INSTITUTION_REQUIRE_CODE);
        }
        return nursingInstitutionRepository.existsByCode(nursingInstitutionCode);
    }

    @Transactional
    public Long saveNursingInstitution(NursingInstitutionCreateRequest request) {

        if (nursingInstitutionRepository.existsByAddress_StreetAddress(request.streetAddress())) {
            throw new NursingInstitutionException(NURSING_INSTITUTION_ALREADY_EXISTS);
        }

        EnumSet<FacilityType> facilityTypes =
                request.facilityTypeList() == null || request.facilityTypeList().isEmpty()
                        ? EnumSet.noneOf(FacilityType.class)
                        : EnumSet.copyOf(request.facilityTypeList());

        NursingInstitution newInstitution = NursingInstitution.create( // 프론트에서 받은 ID 사용
                request.institutionName(),
                request.institutionCode(),
                request.openYear(),
                facilityTypes,
                request.phoneNumber(),
                request.streetAddress(),
                request.detailAddress(),
                request.profileImageUrl());

        nursingInstitutionRepository.save(newInstitution);
        return newInstitution.getId();
    }

    @Transactional
    public NursingInstitutionSearchResponse searchNursingInstitutionByName(String institutionName) {
        List<NursingInstitution> institutions = institutionName == null
                ? nursingInstitutionRepository.findAll()
                : nursingInstitutionRepository.findAllByNameContains(institutionName);
        List<NursingInstitutionSearchResponse.NursingInstitutionSimpleInfo> result = institutions.stream()
                .map(institution -> new NursingInstitutionSearchResponse.NursingInstitutionSimpleInfo(
                        institution.getId(),
                        institution.getName(),
                        institution.getAddress().getStreetAddress(),
                        institution.getAddress().getDetailAddress()))
                .toList();

        return new NursingInstitutionSearchResponse(result);
    }

    @Transactional(readOnly = true)
    public NursingInstitutionSearchResponse getNursingInstitutionList() {
        List<NursingInstitution> institutions = nursingInstitutionRepository.findAll();

        List<NursingInstitutionSearchResponse.NursingInstitutionSimpleInfo> result = institutions.stream()
                .map(institution -> new NursingInstitutionSearchResponse.NursingInstitutionSimpleInfo(
                        institution.getId(),
                        institution.getName(),
                        institution.getAddress().getStreetAddress(),
                        institution.getAddress().getDetailAddress()))
                .toList();

        return new NursingInstitutionSearchResponse(result);
    }

    @Transactional
    public NursingInstitutionProfileUploadResponse uploadProfileImage(MultipartFile file, String institutionName) {
        try {
            String fileName = generateProfileImageFileName(institutionName);
            String profileImageUrl = fileUtil.upload(file, "nursing-institution-image", fileName);
            return new NursingInstitutionProfileUploadResponse(profileImageUrl);
        } catch (IOException e) {
            throw new NursingInstitutionException(NURSING_INSTITUTION_FAILED_TO_UPLOAD_PROFILE_IMAGE);
        }
    }

    private String generateProfileImageFileName(String institutionName) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(institutionName.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new NursingInstitutionException(NURSING_INSTITUTION_FAILED_TO_UPLOAD_PROFILE_IMAGE);
        }
    }
}
