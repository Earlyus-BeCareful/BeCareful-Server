package com.becareful.becarefulserver.domain.caregiver.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.vo.CaregiverInfo;
import com.becareful.becarefulserver.domain.caregiver.dto.request.CaregiverCreateRequest;
import com.becareful.becarefulserver.domain.caregiver.dto.response.*;
import com.becareful.becarefulserver.domain.caregiver.repository.CareerRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationWorkLocationRepository;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.matching.domain.CompletedMatching;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus;
import com.becareful.becarefulserver.domain.matching.repository.CompletedMatchingRepository;
import com.becareful.becarefulserver.domain.matching.repository.ContractRepository;
import com.becareful.becarefulserver.domain.matching.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.work_location.dto.request.WorkLocationDto;
import com.becareful.becarefulserver.global.exception.exception.CaregiverException;
import com.becareful.becarefulserver.global.exception.exception.SocialWorkerException;
import com.becareful.becarefulserver.global.properties.CookieProperties;
import com.becareful.becarefulserver.global.properties.JwtProperties;
import com.becareful.becarefulserver.global.util.AuthUtil;
import com.becareful.becarefulserver.global.util.FileUtil;
import com.becareful.becarefulserver.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final FileUtil fileUtil;
    private final AuthUtil authUtil;
    private final JwtUtil jwtUtil;
    private final CookieProperties cookieProperties;
    private final JwtProperties jwtProperties;

    public CaregiverHomeResponse getHomeData() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        WorkApplication workApplication = workApplicationRepository
                .findByCaregiver(caregiver)
                .orElseThrow(() -> new CaregiverException(CAREGIVER_WORK_APPLICATION_NOT_EXISTS));
        Integer applicationCount = matchingRepository
                .findByWorkApplicationAndMatchingApplicationStatus(workApplication, MatchingApplicationStatus.지원)
                .size();
        Integer recruitmentCount =
                matchingRepository.findAllByWorkApplication(workApplication).size();

        List<CompletedMatching> myWork = completedMatchingRepository.findByCaregiver(caregiver);

        boolean isWorking = !myWork.isEmpty();
        boolean isApplying = workApplication.isActive();
        List<WorkScheduleResponse> workSchedules = myWork.stream()
                .filter(completedMatching -> completedMatching
                        .getContract()
                        .getWorkDays()
                        .contains(LocalDate.now().getDayOfWeek()))
                .map(WorkScheduleResponse::from)
                .toList();

        return CaregiverHomeResponse.of(
                caregiver, recruitmentCount, applicationCount, isWorking, isApplying, workSchedules);
    }

    public CaregiverMyPageHomeResponse getMyPageHomeData() {
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

    private void validateEssentialAgreement(boolean isAgreedToTerms, boolean isAgreedToCollectPersonalInfo) {
        if (isAgreedToTerms && isAgreedToCollectPersonalInfo) {
            return;
        }

        throw new CaregiverException(CAREGIVER_REQUIRED_AGREEMENT);
    }

    private String getEncodedPassword(String rawPassword) {
        var passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(rawPassword);
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

    @Transactional
    public ChatList getChatList() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        List<Matching> matchingList = matchingRepository.findByCaregiver(caregiver);

        List<ChatList.ChatroomInfo> chatroomInfoList = matchingList.stream()
                .map(matching -> {
                    String timeDifference = getTimeDifferenceString(matching);
                    String recentChat = isContractInCompletedMatching(matching) ? "최종 승인이 확정되었습니다!" : "합격 축하드립니다.";

                    // ChatroomInfo 생성
                    return new ChatList.ChatroomInfo(
                            matching.getId(),
                            matching.getRecruitment()
                                    .getElderly()
                                    .getNursingInstitution()
                                    .getName(),
                            recentChat,
                            timeDifference);
                })
                .collect(Collectors.toList());

        // ChatList 반환
        return new ChatList(chatroomInfoList);
    }

    public LocalDateTime findLatestContractCreatedDate(Matching matching) {
        List<Contract> contracts = contractRepository.findLatestContractByMatching(matching);
        Contract latestContract = contracts.isEmpty() ? null : contracts.get(0);
        return latestContract != null ? latestContract.getCreateDate() : null;
    }

    public boolean isContractInCompletedMatching(Matching matching) {
        List<Contract> contracts = contractRepository.findLatestContractByMatching(matching);
        Contract latestContract = contracts.isEmpty() ? null : contracts.get(0);
        if (latestContract != null) {
            return completedMatchingRepository.existsInCompletedMatching(latestContract.getId());
        }
        return false;
    }

    public String getTimeDifferenceString(Matching matching) {
        // 현재 시간
        LocalDateTime currentTime = LocalDateTime.now();

        // 가장 최신 Contract의 생성 시간
        LocalDateTime contractCreatedTime = findLatestContractCreatedDate(matching);

        // Duration을 사용하여 차이 계산
        Duration duration = Duration.between(contractCreatedTime, currentTime);

        // 차이에 따라 다른 시간 단위로 변환
        if (duration.toHours() < 1) {
            // 1시간 이내이면 분 단위로 반환
            long minutes = duration.toMinutes();
            return minutes + "분 전";
        } else if (duration.toDays() < 1) {
            // 1일 이내이면 시간 단위로 반환
            long hours = duration.toHours();
            return hours + "시간 전";
        } else {
            // 1일 이상이면 일 단위로 반환
            long days = duration.toDays();
            return days + "일 전";
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
        String refreshToken = jwtUtil.createRefreshToken(phoneNumber, institutionRank, associationRank);

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
