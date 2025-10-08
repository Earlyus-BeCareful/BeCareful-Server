package com.becareful.becarefulserver.domain.socialworker.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import com.becareful.becarefulserver.domain.matching.dto.response.SocialWorkerRecruitmentResponse;
import com.becareful.becarefulserver.domain.matching.repository.CompletedMatchingRepository;
import com.becareful.becarefulserver.domain.matching.repository.RecruitmentRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.service.ElderlyDomainService;
import com.becareful.becarefulserver.domain.socialworker.dto.request.ElderlyCreateOrUpdateRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.response.ElderlyDetailResponse;
import com.becareful.becarefulserver.domain.socialworker.dto.response.ElderlyProfileUploadResponse;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.global.exception.exception.ElderlyException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import com.becareful.becarefulserver.global.util.FileUtil;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Transactional
    public Long saveElderly(ElderlyCreateOrUpdateRequest request) {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();

        Elderly elderly = Elderly.create(
                request.name(),
                request.birthday(),
                request.gender(),
                request.residentialLocation(),
                request.detailAddress(),
                request.hasInmate(),
                request.hasPet(),
                request.profileImageUrl(),
                socialworker.getNursingInstitution(),
                request.careLevel(),
                request.healthCondition(),
                EnumSet.copyOf(request.detailCareTypeList()));

        elderlyRepository.save(elderly);
        return elderly.getId();
    }

    @Transactional
    public void updateElderly(Long elderlyId, ElderlyCreateOrUpdateRequest request) {
        authUtil.getLoggedInSocialWorker();
        Elderly elderly =
                elderlyRepository.findById(elderlyId).orElseThrow(() -> new ElderlyException(ELDERLY_NOT_EXISTS));

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
                request.profileImageUrl(),
                request.detailCareTypeList());
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
     * @return List<ElderlySimpleDto>
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

        List<SocialWorkerRecruitmentResponse> responses =
                recruitmentRepository.getRecruitmentResponsesByElderly(elderly);

        return ElderlyDetailResponse.of(elderly, responses);
    }

    @Transactional
    public ElderlyProfileUploadResponse uploadProfileImage(MultipartFile file) {
        authUtil.getLoggedInSocialWorker();
        try {
            String fileName = fileUtil.generateImageFileNameWithSource(
                    DateTimeFormatter.ofPattern("yyyyMMddHHmmssnn").format(LocalDateTime.now()));
            String profileImageUrl = fileUtil.upload(file, "profile-image", fileName);
            return new ElderlyProfileUploadResponse(profileImageUrl);
        } catch (IOException e) {
            throw new ElderlyException(ELDERLY_FAILED_TO_UPLOAD_PROFILE_IMAGE);
        }
    }
}
