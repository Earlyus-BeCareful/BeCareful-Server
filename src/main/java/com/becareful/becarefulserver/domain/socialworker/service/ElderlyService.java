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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
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
    public void updateElderly(Long id, ElderlyUpdateRequest request) {
        Elderly elderly = elderlyRepository.findById(id).orElseThrow(() -> new ElderlyException(ELDERLY_NOT_EXISTS));

        request.name().ifPresent(elderly::updateName);
        request.birthday().ifPresent(elderly::updateBirthday);
        request.inmate().ifPresent(elderly::updateInmate);
        request.pet().ifPresent(elderly::updatePet);
        request.gender().ifPresent(elderly::updateGender);
        request.careLevel().ifPresent(elderly::updateCareLevel);
        if (request.siDo().isPresent()
                || request.siGuGun().isPresent()
                || request.eupMyeonDong().isPresent()) {
            elderly.updateResidentialLocation(
                    request.siDo().orElse(null),
                    request.siGuGun().orElse(null),
                    request.eupMyeonDong().orElse(null));
        }
        request.detailAddress().ifPresent(elderly::updateDetailAddress);
        request.healthCondition().ifPresent(elderly::updateHealthCondition);
        request.profileImageUrl().ifPresent(elderly::updateProfileImageUrl);
        request.detailCareTypeList()
                .ifPresent(detailCareTypes -> elderly.updateDetailCareTypes(EnumSet.copyOf(detailCareTypes)));

        elderlyRepository.save(elderly);
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
            String fileName = generateProfileImageFileName(
                    DateTimeFormatter.ofPattern("yyyyMMddHHmmssnn").format(LocalDateTime.now()));
            String profileImageUrl = fileUtil.upload(file, "profile-image", fileName);
            return new ElderlyProfileUploadResponse(profileImageUrl);
        } catch (IOException e) {
            throw new ElderlyException(ELDERLY_FAILED_TO_UPLOAD_PROFILE_IMAGE);
        }
    }

    private String generateProfileImageFileName(String source) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(source.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new ElderlyException(ELDERLY_FAILED_TO_CREATE_PROFILE_IMAGE_NAME);
        }
    }
}
