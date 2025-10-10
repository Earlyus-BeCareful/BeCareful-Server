package com.becareful.becarefulserver.domain.nursing_institution.service;

import static com.becareful.becarefulserver.global.constant.StaticResourceConstant.*;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.common.dto.request.*;
import com.becareful.becarefulserver.domain.common.dto.response.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.*;
import com.becareful.becarefulserver.domain.nursing_institution.dto.*;
import com.becareful.becarefulserver.domain.nursing_institution.dto.request.*;
import com.becareful.becarefulserver.domain.nursing_institution.dto.response.*;
import com.becareful.becarefulserver.domain.nursing_institution.repository.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import com.becareful.becarefulserver.global.service.*;
import com.becareful.becarefulserver.global.util.*;
import java.io.*;
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
    private final S3Util s3Util;
    private final S3Service s3Service;

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

        String profileImageUrl;
        if (request.profileImageTempKey().equals("default")) {
            profileImageUrl = NURSING_INSTITUTION_DEFAULT_PROFILE_IMAGE_URL;
        } else{
            profileImageUrl = s3Util.getPermanentUrlFromTempKey(request.profileImageTempKey());
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
                profileImageUrl);

        nursingInstitutionRepository.save(newInstitution);

        if (!request.profileImageTempKey().equals("default")) {
            try {
                s3Service.moveTempFileToPermanent(request.profileImageTempKey()); // 에러발생시 db롤백
            } catch (Exception e) {
                throw new NursingInstitutionException(NURSING_INSTITUTION_FAILED_TO_MOVE_FILE);
            }
        }

        return newInstitution.getId();
    }

    @Transactional
    public void UpdateNursingInstitutionInfo(UpdateNursingInstitutionInfoRequest request) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        NursingInstitution institution = loggedInSocialWorker.getNursingInstitution();

        String profileImageUrl;
        if (request.profileImageTempKey() == null) {
            profileImageUrl = institution.getProfileImageUrl();
        } else if (request.profileImageTempKey().equals("default")) {
            profileImageUrl = NURSING_INSTITUTION_DEFAULT_PROFILE_IMAGE_URL;
        } else{
            profileImageUrl = s3Util.getPermanentUrlFromTempKey(request.profileImageTempKey());
        }

        institution.updateNursingInstitutionInfo(request, profileImageUrl);

        if (request.profileImageTempKey() != null
                && !request.profileImageTempKey().equals("default")) {
            try {
                s3Service.moveTempFileToPermanent(request.profileImageTempKey()); // 에러발생시 db롤백
            } catch (Exception e) {
                throw new NursingInstitutionException(NURSING_INSTITUTION_FAILED_TO_MOVE_FILE);
            }
        }
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

    // Todo: 삭제
    @Transactional
    public NursingInstitutionProfileUploadResponse uploadProfileImage(MultipartFile file, String institutionName) {
        try {
            String fileName = fileUtil.generateImageFileNameWithSource(institutionName);
            String profileImageUrl = fileUtil.upload(file, "nursing-institution-profile-image/permanent", fileName);
            return new NursingInstitutionProfileUploadResponse(profileImageUrl);
        } catch (IOException e) {
            throw new NursingInstitutionException(NURSING_INSTITUTION_FAILED_TO_UPLOAD_PROFILE_IMAGE);
        }
    }

    public PresignedUrlResponse getPresignedUrl(PresignedUrlRequest request) {
        String newFileName = s3Util.generateImageFileNameWithSource(request.fileName());
        return s3Service.createPresignedUrl("nursing-institution-profile-image", newFileName, request.contentType());
    }
}
