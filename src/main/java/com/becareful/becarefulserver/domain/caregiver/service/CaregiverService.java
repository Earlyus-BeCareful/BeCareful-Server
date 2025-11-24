package com.becareful.becarefulserver.domain.caregiver.service;

import static com.becareful.becarefulserver.global.constant.StaticResourceConstant.*;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.caregiver.domain.vo.*;
import com.becareful.becarefulserver.domain.caregiver.dto.request.*;
import com.becareful.becarefulserver.domain.caregiver.dto.response.*;
import com.becareful.becarefulserver.domain.caregiver.repository.*;
import com.becareful.becarefulserver.domain.chat.repository.*;
import com.becareful.becarefulserver.domain.common.domain.*;
import com.becareful.becarefulserver.domain.common.dto.request.*;
import com.becareful.becarefulserver.domain.common.dto.response.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import com.becareful.becarefulserver.global.service.*;
import com.becareful.becarefulserver.global.util.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaregiverService {

    private final CaregiverRepository caregiverRepository;
    private final WorkApplicationRepository workApplicationRepository;
    private final CompletedMatchingRepository completedMatchingRepository;
    private final CaregiverChatReadStatusRepository caregiverChatReadStatusRepository;
    private final FileUtil fileUtil;
    private final AuthUtil authUtil;
    private final S3Util s3Util;
    private final S3Service s3Service;
    private final CareerDetailRepository careerDetailRepository;
    private final CareerRepository careerRepository;
    private final ApplicationRepository applicationRepository;
    private final RecruitmentRepository recruitmentRepository;

    public CaregiverHomeResponse getHomeData() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();

        boolean hasNewChat = caregiverChatReadStatusRepository.existsUnreadChat(caregiver);

        Optional<WorkApplication> optionalWorkApplication = workApplicationRepository.findByCaregiver(caregiver);

        int applicationCount = 0;
        Long recruitmentCount = recruitmentRepository.countByIsRecruiting();
        boolean isApplying = false;
        if (optionalWorkApplication.isPresent()) {
            WorkApplication workApplication = optionalWorkApplication.get();
            applicationCount = applicationRepository.countByWorkApplication(workApplication);
            isApplying = workApplication.isActive();
        }

        List<CompletedMatching> myWork = completedMatchingRepository.findByCaregiver(caregiver);

        boolean isWorking = !myWork.isEmpty();

        List<WorkScheduleResponse> workSchedules = myWork.stream()
                .filter(completedMatching -> completedMatching
                        .getContract()
                        .getWorkDays()
                        .contains(LocalDate.now().getDayOfWeek()))
                .map(WorkScheduleResponse::from)
                .toList();

        return CaregiverHomeResponse.of(
                caregiver, hasNewChat, applicationCount, recruitmentCount, isWorking, isApplying, workSchedules);
    }

    public CaregiverMyPageHomeResponse getCaregiverMyPageHomeData() {
        Caregiver loggedInCaregiver = authUtil.getLoggedInCaregiver();
        Career career = careerRepository.findByCaregiver(loggedInCaregiver).orElse(null);
        List<CareerDetail> careerDetails = career == null ? List.of() : careerDetailRepository.findAllByCareer(career);
        WorkApplication workApplication =
                workApplicationRepository.findByCaregiver(loggedInCaregiver).orElse(null);
        return CaregiverMyPageHomeResponse.of(loggedInCaregiver, career, careerDetails, workApplication);
    }

    @Transactional
    public Long saveCaregiver(CaregiverCreateRequest request) {
        validateEssentialAgreement(request.isAgreedToTerms(), request.isAgreedToCollectPersonalInfo());

        if (caregiverRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new CaregiverException(CAREGIVER_ALREADY_EXISTS);
        }

        LocalDate birthDate = parseBirthDate(String.valueOf(request.birthYymmdd()), request.genderCode());
        Gender gender = Gender.fromGenderCode(request.genderCode());

        CaregiverInfo caregiverInfo = CaregiverInfo.builder()
                .isHavingCar(request.isHavingCar())
                .isCompleteDementiaEducation(request.isCompleteDementiaEducation())
                .caregiverCertificate(request.caregiverCertificate())
                .socialWorkerCertificate(request.socialWorkerCertificate())
                .nursingCareCertificate(request.nursingCareCertificate())
                .build();

        String profileImageUrl;
        if (request.profileImageTempKey().equals("default")) {
            profileImageUrl = CAREGIVER_DEFAULT_PROFILE_IMAGE_URL;
        } else {
            profileImageUrl = s3Util.getPermanentUrlFromTempKey(request.profileImageTempKey());
        }

        Caregiver caregiver = Caregiver.create(
                request.realName(),
                birthDate,
                gender,
                request.phoneNumber(),
                profileImageUrl,
                request.streetAddress(),
                request.detailAddress(),
                caregiverInfo,
                request.isAgreedToReceiveMarketingInfo());

        caregiverRepository.save(caregiver);

        if (!request.profileImageTempKey().equals("default")) {
            try {
                s3Service.moveTempFileToPermanent(request.profileImageTempKey()); // 에러발생시 db롤백
            } catch (Exception e) {
                throw new CaregiverException(CAREGIVER_FAILED_TO_MOVE_FILE);
            }
        }

        return caregiver.getId();
    }

    // TODO: 메서드 삭제
    @Transactional
    public CaregiverProfileUploadResponse uploadProfileImage(MultipartFile file) {
        try {
            String fileName = fileUtil.generateRandomImageFileName();
            String profileImageUrl = fileUtil.upload(file, "caregiver-profile-image/permanent", fileName);
            return new CaregiverProfileUploadResponse(profileImageUrl);
        } catch (IOException e) {
            throw new CaregiverException(FAILED_TO_CREATE_IMAGE_FILE_NAME);
        }
    }

    public PresignedUrlResponse getPresignedUrl(ProfileImagePresignedUrlRequest request) {
        String newFileName = s3Util.generateImageFileNameWithSource(request.fileName());
        return s3Service.createPresignedUrl("caregiver-profile-image", newFileName, request.contentType());
    }

    @Transactional
    public void updateCaregiverInfo(MyPageUpdateRequest request) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();

        String profileImageUrl;
        if (request.profileImageTempKey() == null) {
            profileImageUrl = caregiver.getProfileImageUrl();
        } else if (request.profileImageTempKey().equals("default")) {
            profileImageUrl = CAREGIVER_DEFAULT_PROFILE_IMAGE_URL;
        } else {
            profileImageUrl = s3Util.getPermanentUrlFromTempKey(request.profileImageTempKey());
        }

        CaregiverInfo caregiverInfo = new CaregiverInfo(
                request.isHavingCar(),
                request.isCompleteDementiaEducation(),
                request.caregiverCertificate(),
                request.socialWorkerCertificate(),
                request.nursingCareCertificate());

        caregiver.updateInfo(request.phoneNumber(), profileImageUrl, caregiverInfo, request.address());

        if (request.profileImageTempKey() != null
                && !request.profileImageTempKey().equals("default")) {
            try {
                s3Service.moveTempFileToPermanent(request.profileImageTempKey()); // 에러발생시 db롤백
            } catch (Exception e) {
                throw new CaregiverException(CAREGIVER_FAILED_TO_MOVE_FILE);
            }
        }
    }

    @Transactional
    public void deleteCaregiver(HttpServletResponse response) {
        /**
         * 회원 탈퇴에 대한 명확한 기획이 필요함.
         * 1. hard delete / soft delete
         * 2. soft delete 라면 어떤 데이터를 마스킹할 것인지
         * 3. 요양보호사와 연괸된 데이터 중 어떤 것을 남기고 어떤 것을 삭제할 지
         * 4. 어떤 상황에서 탈퇴가 가능하고, 탈퇴가 불가능한지
         */
        Caregiver loggedInCaregiver = authUtil.getLoggedInCaregiver();
        caregiverRepository.delete(loggedInCaregiver);
        authUtil.logout(response);
    }

    private void validateEssentialAgreement(boolean isAgreedToTerms, boolean isAgreedToCollectPersonalInfo) {
        if (isAgreedToTerms && isAgreedToCollectPersonalInfo) {
            return;
        }

        throw new CaregiverException(CAREGIVER_REQUIRED_AGREEMENT);
    }

    private LocalDate parseBirthDate(String yymmdd, int genderCode) {
        int yearPrefix;
        switch (genderCode) {
            case 1:
            case 2:
                yearPrefix = 1900;
                break;
            case 3:
            case 4:
                yearPrefix = 2000;
                break;
            default:
                throw new IllegalArgumentException("Invalid gender code: " + genderCode);
        }

        int year = yearPrefix + Integer.parseInt(yymmdd.substring(0, 2));
        int month = Integer.parseInt(yymmdd.substring(2, 4));
        int day = Integer.parseInt(yymmdd.substring(4, 6));

        return LocalDate.of(year, month, day);
    }
}
