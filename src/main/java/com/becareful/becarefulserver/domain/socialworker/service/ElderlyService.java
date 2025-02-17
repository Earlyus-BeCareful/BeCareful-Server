package com.becareful.becarefulserver.domain.socialworker.service;

import com.becareful.becarefulserver.domain.caregiver.dto.response.CaregiverProfileUploadResponse;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;
import com.becareful.becarefulserver.domain.socialworker.dto.request.ElderlyCreateRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.response.ElderlyProfileUploadResponse;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.domain.socialworker.repository.NursingInstitutionRepository;
import com.becareful.becarefulserver.global.exception.exception.CaregiverException;
import com.becareful.becarefulserver.global.exception.exception.ElderlyException;
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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class ElderlyService {
    private final ElderlyRepository elderlyRepository;
    private final NursingInstitutionRepository nursingInstitutionRepository;
    private final FileUtil fileUtil;


    @Transactional
    public Long saveElderly(ElderlyCreateRequest request) {

        NursingInstitution institution = nursingInstitutionRepository.findById(request.institutionId())
                .orElseThrow(() -> new NursingInstitutionException(NURSING_INSTITUTION_NOT_FOUND));



        Elderly elderly = Elderly.create(
                request.name(), request.birthday(), request.gender(),
                request.inmate(), request.pet(), request.profileImageUrl(),
                institution, request.careLevel(), request.healthCondition(), EnumSet.copyOf(request.detailCareTypeList()
                ));

        elderlyRepository.save(elderly);
        return elderly.getId();
    }
    @Transactional
    public ElderlyProfileUploadResponse uploadProfileImage(MultipartFile file, String institutionId) {
        try {
            String fileName = generateProfileImageFileName(institutionId);
            String profileImageUrl = fileUtil.upload(file, fileName);
            return new ElderlyProfileUploadResponse(profileImageUrl);
        } catch (IOException e) {
            throw new ElderlyException(ELDERLY_FAILED_TO_UPLOAD_PROFILE_IMAGE);
        }
    }
    private String generateProfileImageFileName(String institutionId) {
        try {
            var md  = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(institutionId.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new ElderlyException(
                    ELDERLY_FAILED_TO_UPLOAD_PROFILE_IMAGE);
        }
    }
}
