package com.becareful.becarefulserver.domain.socialworker.service;

import com.becareful.becarefulserver.domain.socialworker.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.dto.request.NursingInstitutionCreateRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.response.NursingInstitutionProfileUploadResponse;
import com.becareful.becarefulserver.domain.socialworker.repository.NursingInstitutionRepository;
import com.becareful.becarefulserver.global.exception.exception.NursingInstitutionException;
import com.becareful.becarefulserver.global.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.CAREGIVER_FAILED_TO_UPLOAD_PROFILE_IMAGE;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.NURSING_INSTITUTION_FAILED_TO_UPLOAD_PROFILE_IMAGE;

@Service
@RequiredArgsConstructor
public class NursingInstitutionService {
    private final NursingInstitutionRepository nursingInstitutionRepository;
    private final FileUtil fileUtil;
    @Transactional
    public boolean existsById(String institutionId) {
        return nursingInstitutionRepository.existsById(institutionId);
    }
    @Transactional
    public String saveNursingInstitution(NursingInstitutionCreateRequest request){
        NursingInstitution newInstitution = NursingInstitution.create(
                request.institutionId(), // 프론트에서 받은 ID 사용
                request.institutionName(),
                request.streetAddress(), request.detailAddress(),
                request.phoneNumber(), request.isHavingBathCar(),
                request.openDate(), request.profileImageUrl()
        );

        nursingInstitutionRepository.save(newInstitution);
        return newInstitution.getId();
    }

    @Transactional
    public NursingInstitutionProfileUploadResponse uploadProfileImage(MultipartFile file, String institutionId) {
        try {
            String fileName = generateProfileImageFileName(institutionId);
            String profileImageUrl = fileUtil.upload(file, fileName);
            return new NursingInstitutionProfileUploadResponse(profileImageUrl);
        } catch (IOException e) {
            throw new NursingInstitutionException(NURSING_INSTITUTION_FAILED_TO_UPLOAD_PROFILE_IMAGE);
        }
    }
    private String generateProfileImageFileName(String institutionId) {
        try {
            var md  = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(institutionId.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new NursingInstitutionException(
                    NURSING_INSTITUTION_FAILED_TO_UPLOAD_PROFILE_IMAGE);
        }
    }

}
