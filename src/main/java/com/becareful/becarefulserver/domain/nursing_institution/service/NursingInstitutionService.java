package com.becareful.becarefulserver.domain.nursing_institution.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.nursing_institution.domain.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.FacilityType;
import com.becareful.becarefulserver.domain.nursing_institution.dto.InstitutionSimpleDto;
import com.becareful.becarefulserver.domain.nursing_institution.dto.request.*;
import com.becareful.becarefulserver.domain.nursing_institution.dto.response.*;
import com.becareful.becarefulserver.domain.nursing_institution.repository.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import com.becareful.becarefulserver.global.util.*;
import java.io.*;
import java.nio.charset.*;
import java.security.*;
import java.util.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.*;

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
    public void UpdateNursingInstitutionInfo(UpdateNursingInstitutionInfoRequest request) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        NursingInstitution institution = loggedInSocialWorker.getNursingInstitution();

        institution.updateNursingInstitutionInfo(request);
    }

    @Transactional(readOnly = true)
    public List<InstitutionSimpleDto> searchNursingInstitutionByName(String institutionName) {
        List<NursingInstitution> institutions = institutionName == null
                ? nursingInstitutionRepository.findAll()
                : nursingInstitutionRepository.findAllByNameContains(institutionName);
        return institutions.stream().map(InstitutionSimpleDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<InstitutionSimpleDto> getNursingInstitutionList() {
        List<NursingInstitution> institutions = nursingInstitutionRepository.findAll();
        return institutions.stream().map(InstitutionSimpleDto::from).toList();
    }

    @Transactional
    public NursingInstitutionProfileUploadResponse uploadProfileImage(MultipartFile file, String institutionName) {
        try {
            String fileName = fileUtil.generateProfileImageFileNameWithSource(institutionName);
            String profileImageUrl = fileUtil.upload(file, "nursing-institution-image", fileName);
            return new NursingInstitutionProfileUploadResponse(profileImageUrl);
        } catch (IOException e) {
            throw new NursingInstitutionException(NURSING_INSTITUTION_FAILED_TO_UPLOAD_PROFILE_IMAGE);
        }
    }
}
