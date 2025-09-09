package com.becareful.becarefulserver.domain.caregiver.service;

import static com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus.*;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.caregiver.domain.vo.*;
import com.becareful.becarefulserver.domain.caregiver.dto.request.*;
import com.becareful.becarefulserver.domain.caregiver.dto.response.*;
import com.becareful.becarefulserver.domain.caregiver.repository.*;
import com.becareful.becarefulserver.domain.chat.service.*;
import com.becareful.becarefulserver.domain.common.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import com.becareful.becarefulserver.domain.work_location.dto.request.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import com.becareful.becarefulserver.global.properties.*;
import com.becareful.becarefulserver.global.util.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.charset.*;
import java.security.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaregiverService {

    private final CaregiverRepository caregiverRepository;
    private final CareerRepository careerRepository;
    private final WorkApplicationRepository workApplicationRepository;
    private final WorkApplicationWorkLocationRepository workApplicationWorkLocationRepository;
    private final MatchingRepository matchingRepository;
    private final CompletedMatchingRepository completedMatchingRepository;
    private final ContractRepository contractRepository;
    private final CaregiverChatService chatService;
    private final FileUtil fileUtil;
    private final AuthUtil authUtil;
    private final JwtUtil jwtUtil;
    private final CookieProperties cookieProperties;
    private final JwtProperties jwtProperties;
    private final CookieUtil cookieUtil;

    public CaregiverHomeResponse getHomeData() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();

        boolean hasNewChat = chatService.checkNewChat(caregiver);

        Optional<WorkApplication> optionalWorkApplication = workApplicationRepository.findByCaregiver(caregiver);

        Integer applicationCount = 0;
        boolean isApplying = false;
        if (optionalWorkApplication.isPresent()) {
            WorkApplication workApplication = optionalWorkApplication.get();
            applicationCount = matchingRepository
                    .findByWorkApplicationAndMatchingApplicationStatus(workApplication, 지원검토중)
                    .size();
            isApplying = workApplication.isActive();
        }
        Integer recruitmentCount = matchingRepository
                .findAllByCaregiverAndApplicationStatus(caregiver, 미지원)
                .size();

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
                caregiver, hasNewChat, recruitmentCount, applicationCount, isWorking, isApplying, workSchedules);
    }

    public CaregiverMyPageHomeResponse getCaregiverMyPageHomeData() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        Career career = careerRepository.findByCaregiver(caregiver).orElse(null);
        WorkApplication workApplication =
                workApplicationRepository.findByCaregiver(caregiver).orElse(null);
        List<WorkLocationDto> locations =
                workApplicationWorkLocationRepository.findAllByWorkApplication(workApplication).stream()
                        .map(data -> WorkLocationDto.from(data.getWorkLocation()))
                        .toList();
        return CaregiverMyPageHomeResponse.of(caregiver, career, workApplication, locations);
    }

    @Transactional
    public Long saveCaregiver(CaregiverCreateRequest request, HttpServletResponse httpServletResponse) {
        validateEssentialAgreement(request.isAgreedToTerms(), request.isAgreedToCollectPersonalInfo());

        if (caregiverRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new CaregiverException(CAREGIVER_ALREADY_EXISTS);
        }

        LocalDate birthDate = parseBirthDate(String.valueOf(request.birthYymmdd()), request.genderCode());
        Gender gender = parseGender(request.genderCode());

        CaregiverInfo caregiverInfo = CaregiverInfo.builder()
                .isHavingCar(request.isHavingCar())
                .isCompleteDementiaEducation(request.isCompleteDementiaEducation())
                .caregiverCertificate(request.caregiverCertificate())
                .socialWorkerCertificate(request.socialWorkerCertificate())
                .nursingCareCertificate(request.nursingCareCertificate())
                .build();

        Caregiver caregiver = Caregiver.create(
                request.realName(),
                birthDate,
                gender,
                request.phoneNumber(),
                request.profileImageUrl(),
                request.streetAddress(),
                request.detailAddress(),
                caregiverInfo,
                request.isAgreedToReceiveMarketingInfo());

        caregiverRepository.save(caregiver);

        updateJwtAndSecurityContext(httpServletResponse, request.phoneNumber());

        return caregiver.getId();
    }

    @Transactional
    public CaregiverProfileUploadResponse uploadProfileImage(MultipartFile file) {
        try {
            String fileName = generateProfileImageFileName();
            String profileImageUrl = fileUtil.upload(file, "profile-image", fileName);
            return new CaregiverProfileUploadResponse(profileImageUrl);
        } catch (IOException e) {
            throw new CaregiverException(CAREGIVER_FAILED_TO_UPLOAD_PROFILE_IMAGE);
        }
    }

    @Transactional
    public void updateCaregiverInfo(MyPageUpdateRequest request) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        CaregiverInfo caregiverInfo = new CaregiverInfo(
                request.isHavingCar(),
                request.isCompleteDementiaEducation(),
                request.caregiverCertificate(),
                request.socialWorkerCertificate(),
                request.nursingCareCertificate());
        caregiver.updateInfo(request.phoneNumber(), caregiverInfo);
    }
    public void logout(HttpServletResponse response) {
        response.addCookie(cookieUtil.deleteCookie("AccessToken"));
        response.addCookie(cookieUtil.deleteCookie("RefreshToken"));
        SecurityContextHolder.clearContext();
    }

    @Transactional
    public void leave(HttpServletResponse response) {
        Caregiver loggedInCaregiver = authUtil.getLoggedInCaregiver();
        matchingRepository.deleteAllByCaregiverAndStatusNot(loggedInCaregiver,합격);
        caregiverRepository.delete(loggedInCaregiver);

        response.addCookie(cookieUtil.deleteCookie("AccessToken"));
        response.addCookie(cookieUtil.deleteCookie("RefreshToken"));
        SecurityContextHolder.clearContext();
    }
    private void validateEssentialAgreement(boolean isAgreedToTerms, boolean isAgreedToCollectPersonalInfo) {
        if (isAgreedToTerms && isAgreedToCollectPersonalInfo) {
            return;
        }

        throw new CaregiverException(CAREGIVER_REQUIRED_AGREEMENT);
    }

    private String generateProfileImageFileName() {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new CaregiverException(CAREGIVER_FAILED_TO_UPLOAD_PROFILE_IMAGE);
        }
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

    private Gender parseGender(int genderCode) {
        switch (genderCode) {
            case 1:
            case 3:
                return Gender.MALE;
            case 2:
            case 4:
                return Gender.FEMALE;
            default:
                throw new SocialWorkerException(USER_CREATE_INVALID_GENDER_CODE);
        }
    }

    private void updateJwtAndSecurityContext(HttpServletResponse response, String phoneNumber) {
        String institutionRank = "NONE";
        String associationRank = "NONE";

        String accessToken = jwtUtil.createAccessToken(phoneNumber, institutionRank, associationRank);
        String refreshToken = jwtUtil.createRefreshToken(phoneNumber);

        response.addCookie(createCookie("AccessToken", accessToken, jwtProperties.getAccessTokenExpiry()));
        response.addCookie(createCookie("RefreshToken", refreshToken, jwtProperties.getRefreshTokenExpiry()));

        List<GrantedAuthority> authorities =
                List.of((GrantedAuthority) () -> institutionRank, (GrantedAuthority) () -> associationRank);

        Authentication auth = new UsernamePasswordAuthenticationToken(phoneNumber, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private Cookie createCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(cookieProperties.getCookieSecure());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", cookieProperties.getCookieSameSite());
        return cookie;
    }
}
