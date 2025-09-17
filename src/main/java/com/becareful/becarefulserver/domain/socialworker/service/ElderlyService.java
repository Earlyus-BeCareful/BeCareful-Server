package com.becareful.becarefulserver.domain.socialworker.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.matching.repository.CompletedMatchingRepository;
import com.becareful.becarefulserver.domain.matching.repository.RecruitmentRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.dto.request.ElderlyCreateRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.request.ElderlyUpdateRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.response.ElderlyInfoResponse;
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
    private final FileUtil fileUtil;
    private final AuthUtil authUtil;

    @Transactional
    public Long saveElderly(ElderlyCreateRequest request) {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();

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
                request.profileImageUrl(),
                socialworker.getNursingInstitution(),
                request.careLevel(),
                request.healthCondition(),
                EnumSet.copyOf(request.detailCareTypeList()));

        elderlyRepository.save(elderly);
        return elderly.getId();
    }

    @Transactional
    public void updateElderly(Long elderlyId, ElderlyUpdateRequest request) {
        authUtil.getLoggedInSocialWorker();
        Elderly elderly =
                elderlyRepository.findById(elderlyId).orElseThrow(() -> new ElderlyException(ELDERLY_NOT_EXISTS));

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
                request.profileImageUrl(),
                request.detailCareTypeList());
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
        List<Elderly> elderlyList = elderlyRepository.findByNursingInstitution(socialworker.getNursingInstitution());

        return elderlyList.stream()
                .map(elderly -> {
                    boolean hasRecruitment = recruitmentRepository.existsByElderly(elderly);
                    int caregiverNum = completedMatchingRepository.countDistinctCaregiversByElderly(elderly);
                    return ElderlyInfoResponse.of(elderly, caregiverNum, hasRecruitment);
                })
                .toList();
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
