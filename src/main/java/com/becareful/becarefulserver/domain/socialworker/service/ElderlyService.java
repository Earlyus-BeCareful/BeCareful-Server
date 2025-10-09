package com.becareful.becarefulserver.domain.socialworker.service;

import static com.becareful.becarefulserver.global.constant.StaticResourceConstant.CAREGIVER_DEFAULT_PROFILE_IMAGE_URL;
import static com.becareful.becarefulserver.global.constant.StaticResourceConstant.ELDERLY_DEFAULT_PROFILE_IMAGE_URL;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.common.dto.request.*;
import com.becareful.becarefulserver.domain.common.dto.response.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.domain.socialworker.dto.request.*;
import com.becareful.becarefulserver.domain.socialworker.dto.response.*;
import com.becareful.becarefulserver.domain.socialworker.repository.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import com.becareful.becarefulserver.global.service.*;
import com.becareful.becarefulserver.global.util.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ElderlyService {

    private final ElderlyRepository elderlyRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final CompletedMatchingRepository completedMatchingRepository;
    private final FileUtil fileUtil;
    private final AuthUtil authUtil;
    private final S3Util s3Util;
    private final S3Service s3Service;

    @Transactional
    public Long saveElderly(ElderlyCreateRequest request) {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();

        String profileImageUrl = null;
        if (request.profileImageTempKey().equals("default")) {
            profileImageUrl = CAREGIVER_DEFAULT_PROFILE_IMAGE_URL;
        } else if (request.profileImageTempKey() != null) {
            profileImageUrl = s3Util.getPermanentUrlFromTempKey(request.profileImageTempKey());
        }

        Elderly elderly = Elderly.create(
                request.name(),
                request.birthday(),
                request.gender(),
                request.siDo(),
                request.siGuGun(),
                request.eupMyeonDong(),
                request.detailAddress(),
                request.inmate(),
                request.pet(),
                profileImageUrl,
                socialworker.getNursingInstitution(),
                request.careLevel(),
                request.healthCondition(),
                EnumSet.copyOf(request.detailCareTypeList()));

        if (!request.profileImageTempKey().equals("default")) {
            try {
                s3Service.moveTempFileToPermanent(request.profileImageTempKey()); // 에러발생시 db롤백
            } catch (Exception e) {
                throw new ElderlyException(ELDERLY_FAILED_TO_MOVE_FILE);
            }
        }

        elderlyRepository.save(elderly);
        return elderly.getId();
    }

    @Transactional
    public void updateElderly(Long elderlyId, ElderlyUpdateRequest request) {
        authUtil.getLoggedInSocialWorker();
        Elderly elderly =
                elderlyRepository.findById(elderlyId).orElseThrow(() -> new ElderlyException(ELDERLY_NOT_EXISTS));

        String profileImageUrl = elderly.getProfileImageUrl();
        if (request.profileImageTempKey().equals("default")) {
            profileImageUrl = ELDERLY_DEFAULT_PROFILE_IMAGE_URL;
        } else if (request.profileImageTempKey() != null) {
            profileImageUrl = s3Util.getPermanentUrlFromTempKey(request.profileImageTempKey());
        }

        elderly.update(
                request.name(),
                request.birthday(),
                request.gender(),
                request.inmate(), // TODO : boolean field 에 맞게 네이밍 변경
                request.pet(),
                request.careLevel(),
                request.siDo(), // TODO : Location VO 로 묶기
                request.siGuGun(),
                request.eupMyeonDong(),
                request.detailAddress(),
                request.healthCondition(),
                profileImageUrl,
                request.detailCareTypeList());

        if (request.profileImageTempKey() != null
                && !request.profileImageTempKey().equals("default")) {
            try {
                s3Service.moveTempFileToPermanent(request.profileImageTempKey()); // 에러발생시 db롤백
            } catch (Exception e) {
                throw new ElderlyException(ELDERLY_FAILED_TO_MOVE_FILE);
            }
        }
    }

    public List<ElderlyInfoResponse> getElderlyListBySearch(String searchString) {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();

        List<Elderly> elderlyList = elderlyRepository.findByNursingInstitutionAndNameContaining(
                socialworker.getNursingInstitution(), searchString);

        return elderlyList.stream()
                .map(elderly -> {
                    boolean hasRecruitment = recruitmentRepository.existsByElderly(elderly);
                    int caregiverNum = completedMatchingRepository.countDistinctCaregiversByElderly(elderly);
                    return ElderlyInfoResponse.of(elderly, caregiverNum, hasRecruitment);
                })
                .toList();
    }

    public List<ElderlyInfoResponse> getElderlyList() {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();
        List<Elderly> elderlyList = elderlyRepository.findAllByNursingInstitution(socialworker.getNursingInstitution());

        return elderlyList.stream()
                .map(elderly -> {
                    boolean hasRecruitment = recruitmentRepository.existsByElderly(elderly);
                    int caregiverNum = completedMatchingRepository.countDistinctCaregiversByElderly(elderly);
                    return ElderlyInfoResponse.of(elderly, caregiverNum, hasRecruitment);
                })
                .toList();
    }

    // TODO: 삭제
    @Transactional
    public ElderlyProfileUploadResponse uploadProfileImage(MultipartFile file) {
        authUtil.getLoggedInSocialWorker();
        try {
            String fileName = fileUtil.generateImageFileNameWithSource(
                    DateTimeFormatter.ofPattern("yyyyMMddHHmmssnn").format(LocalDateTime.now()));
            String profileImageUrl = fileUtil.upload(file, "elderly-profile-image/permanent", fileName);
            return new ElderlyProfileUploadResponse(profileImageUrl);
        } catch (IOException e) {
            throw new ElderlyException(ELDERLY_FAILED_TO_UPLOAD_PROFILE_IMAGE);
        }
    }

    public PresignedUrlResponse getPresignedUrl(PresignedUrlRequest request) {
        String newFileName = s3Util.generateImageFileNameWithSource(request.fileName());
        return s3Service.createPresignedUrl("elderly-profile-image", newFileName, request.contentType());
    }
}
