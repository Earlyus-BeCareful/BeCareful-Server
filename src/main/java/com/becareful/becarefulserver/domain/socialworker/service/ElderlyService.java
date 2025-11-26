package com.becareful.becarefulserver.domain.socialworker.service;

import static com.becareful.becarefulserver.global.constant.StaticResourceConstant.*;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationRepository;
import com.becareful.becarefulserver.domain.common.dto.request.*;
import com.becareful.becarefulserver.domain.common.dto.response.*;
import com.becareful.becarefulserver.domain.matching.domain.service.MatchingDomainService;
import com.becareful.becarefulserver.domain.matching.dto.*;
import com.becareful.becarefulserver.domain.matching.dto.response.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.service.*;
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
import org.springframework.data.domain.*;
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
    private final ElderlyDomainService elderlyDomainService;
    private final FileUtil fileUtil;
    private final AuthUtil authUtil;
    private final S3Util s3Util;
    private final S3Service s3Service;
    private final ApplicationRepository applicationRepository;
    private final WorkApplicationRepository workApplicationRepository;
    private final MatchingDomainService matchingDomainService;

    @Transactional
    public Long saveElderly(ElderlyCreateRequest request) {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();

        String profileImageUrl;
        if (request.profileImageTempKey().equals("default")) {
            profileImageUrl = CAREGIVER_DEFAULT_PROFILE_IMAGE_URL;
        } else {
            profileImageUrl = s3Util.getPermanentUrlFromTempKey(request.profileImageTempKey());
        }

        Elderly elderly = Elderly.create(
                request.name(),
                request.birthday(),
                request.gender(),
                request.residentialLocation(),
                request.detailAddress(),
                request.hasInmate(),
                request.hasPet(),
                profileImageUrl,
                socialworker.getNursingInstitution(),
                request.careLevel(),
                request.healthCondition(),
                EnumSet.copyOf(request.detailCareTypeList()));

        elderlyRepository.save(elderly);

        if (!request.profileImageTempKey().equals("default")) {
            try {
                s3Service.moveTempFileToPermanent(request.profileImageTempKey()); // 에러발생시 db롤백
            } catch (Exception e) {
                throw new ElderlyException(ELDERLY_FAILED_TO_MOVE_FILE);
            }
        }
        return elderly.getId();
    }

    @Transactional
    public void updateElderly(Long elderlyId, ElderlyUpdateRequest request) {
        authUtil.getLoggedInSocialWorker();
        Elderly elderly =
                elderlyRepository.findById(elderlyId).orElseThrow(() -> new ElderlyException(ELDERLY_NOT_EXISTS));

        String profileImageUrl;
        if (request.profileImageTempKey() == null) {
            profileImageUrl = elderly.getProfileImageUrl();
        } else if (request.profileImageTempKey().equals("default")) {
            profileImageUrl = ELDERLY_DEFAULT_PROFILE_IMAGE_URL;
        } else {
            profileImageUrl = s3Util.getPermanentUrlFromTempKey(request.profileImageTempKey());
        }

        elderly.update(
                request.name(),
                request.birthday(),
                request.gender(),
                request.hasInmate(),
                request.hasPet(),
                request.careLevel(),
                request.residentialLocation(),
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

    /**
     * 3.3.2 어르신 목록 조회
     * @param pageable
     * @return Page<ElderlySimpleDto>
     */
    @Transactional(readOnly = true)
    public Page<ElderlySimpleDto> getElderlyList(Pageable pageable) {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();
        Page<Elderly> elderlyList =
                elderlyRepository.findPageByNursingInstitution(socialworker.getNursingInstitution(), pageable);
        return elderlyList.map(ElderlySimpleDto::from);
    }

    /**
     * 3.3.2 어르신 목록 - 어르신 검색
     * @param keyword
     * @return Page<ElderlySimpleDto>
     */
    @Transactional(readOnly = true)
    public Page<ElderlySimpleDto> searchElderly(String keyword, Pageable pageable) {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();

        Page<Elderly> elderlyList = elderlyRepository.findByNursingInstitutionAndNameContaining(
                socialworker.getNursingInstitution(), keyword, pageable);

        return elderlyList.map(ElderlySimpleDto::from);
    }

    /**
     * 3.2.1.2 공고 등록 - 어르신 상세 정보 조회
     * @param elderlyId
     * @return
     */
    @Transactional(readOnly = true)
    public ElderlyDetailResponse getElderlyDetail(Long elderlyId) {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();

        Elderly elderly =
                elderlyRepository.findById(elderlyId).orElseThrow(() -> new ElderlyException(ELDERLY_NOT_EXISTS));

        elderlyDomainService.validateElderlyAndSocialWorkerInstitution(elderly, socialworker);

        List<WorkApplication> workApplications = workApplicationRepository.findAllActiveWorkApplication();

        List<SocialWorkerRecruitmentResponse> responses = recruitmentRepository.findAllByElderly(elderly).stream()
                .map(recruitment -> {
                    long applicationCount = applicationRepository.countByRecruitment(recruitment);
                    long matchingCount = workApplications.stream()
                            .filter(workApplication -> matchingDomainService.isMatched(workApplication, recruitment))
                            .count();

                    return SocialWorkerRecruitmentResponse.of(recruitment, applicationCount, matchingCount);
                })
                .toList();

        return ElderlyDetailResponse.of(elderly, responses);
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

    public PresignedUrlResponse getPresignedUrl(ProfileImagePresignedUrlRequest request) {
        String newFileName = s3Util.generateImageFileNameWithSource(request.fileName());
        return s3Service.createPresignedUrl("elderly-profile-image", newFileName, request.contentType());
    }
}
