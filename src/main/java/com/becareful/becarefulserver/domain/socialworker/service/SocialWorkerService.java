package com.becareful.becarefulserver.domain.socialworker.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.repository.AssociationRepository;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.repository.CompletedMatchingRepository;
import com.becareful.becarefulserver.domain.matching.repository.ContractRepository;
import com.becareful.becarefulserver.domain.matching.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.repository.NursingInstitutionRepository;
import com.becareful.becarefulserver.domain.nursing_institution.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import com.becareful.becarefulserver.domain.socialworker.dto.request.SocialWorkerCreateRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.request.SocialWorkerUpdateBasicInfoRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.response.ChatList;
import com.becareful.becarefulserver.domain.socialworker.dto.response.SimpleElderlyResponse;
import com.becareful.becarefulserver.domain.socialworker.dto.response.SocialWorkerHomeResponse;
import com.becareful.becarefulserver.domain.socialworker.dto.response.SocialWorkerMyInfo;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.global.exception.exception.NursingInstitutionException;
import com.becareful.becarefulserver.global.exception.exception.SocialWorkerException;
import com.becareful.becarefulserver.global.properties.CookieProperties;
import com.becareful.becarefulserver.global.properties.JwtProperties;
import com.becareful.becarefulserver.global.util.AuthUtil;
import com.becareful.becarefulserver.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialWorkerService {
    private final SocialWorkerRepository socialworkerRepository;
    private final NursingInstitutionRepository nursingInstitutionRepository;

    private final MatchingRepository matchingRepository;
    private final ContractRepository contractRepository;
    private final CompletedMatchingRepository completedMatchingRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ElderlyRepository elderlyRepository;
    private final AssociationRepository associationRepository;

    private final AuthUtil authUtil;
    private final JwtUtil jwtUtil;
    private final CookieProperties cookieProperties;
    private final JwtProperties jwtProperties;

    public SocialWorkerHomeResponse getHomeData() {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        Integer elderlyCount = elderlyRepository
                .findByNursingInstitution(loggedInSocialWorker.getNursingInstitution())
                .size();
        Integer socialWorkerCount =
                socialworkerRepository.countByNursingInstitution(loggedInSocialWorker.getNursingInstitution());

        List<Long> elderlyIds =
                elderlyRepository.findByNursingInstitution(loggedInSocialWorker.getNursingInstitution()).stream()
                        .map(Elderly::getId)
                        .toList();

        List<Matching> matchingList = matchingRepository.findAllMatchingByElderlyIds(elderlyIds);

        Long processingMatchingCount = matchingList.stream()
                .filter(matching -> matching.getMatchingApplicationStatus().equals(MatchingApplicationStatus.지원검토중))
                .count();

        Long recentlyMatchedCount = matchingList.stream()
                .filter(matching ->
                        matching.getUpdateDate().isAfter(LocalDateTime.now().minusDays(7)))
                .filter(matching -> matching.getMatchingApplicationStatus().equals(MatchingApplicationStatus.합격))
                .count();

        Integer totalMatchedCount = matchingList.size();

        Integer appliedCaregiverCount = matchingList.stream()
                .filter(matching -> matching.getMatchingApplicationStatus().equals(MatchingApplicationStatus.지원검토중))
                .map(matching -> matching.getWorkApplication().getId())
                .collect(Collectors.toSet())
                .size();

        long completedRecruitmentCount = matchingList.stream()
                .map(Matching::getRecruitment)
                .filter(recruitment -> !recruitment.isRecruiting())
                .count();

        long wholeApplierCountForCompletedRecruitment = matchingList.stream()
                .filter(matching -> !matching.getRecruitment().isRecruiting())
                .filter(matching -> matching.getMatchingApplicationStatus().equals(MatchingApplicationStatus.합격)
                        || matching.getMatchingApplicationStatus().equals(MatchingApplicationStatus.지원거절))
                .count();

        long wholeCompletedMatchingCount = matchingList.stream()
                .filter(matching -> !matching.getRecruitment().isRecruiting())
                .count();

        List<SimpleElderlyResponse> elderlyList = matchingList.stream()
                .map(Matching::getRecruitment)
                .filter(Recruitment::isRecruiting)
                .map(Recruitment::getElderly)
                .map(SimpleElderlyResponse::from)
                .toList();

        return SocialWorkerHomeResponse.of(
                loggedInSocialWorker,
                elderlyCount,
                socialWorkerCount,
                processingMatchingCount,
                recentlyMatchedCount,
                totalMatchedCount,
                appliedCaregiverCount,
                ((double) wholeApplierCountForCompletedRecruitment / completedRecruitmentCount) * 100,
                ((double) wholeApplierCountForCompletedRecruitment / wholeCompletedMatchingCount) * 100,
                elderlyList);
    }

    @Transactional(readOnly = true)
    public boolean checkSameNickNameAtRegist(String nickName) {
        return socialworkerRepository.existsByNickname(nickName);
    }

    @Transactional
    public Long saveSocialworker(SocialWorkerCreateRequest request, HttpServletResponse httpServletResponse) {

        validateEssentialAgreement(request.isAgreedToTerms(), request.isAgreedToCollectPersonalInfo());

        // 기관ID로 기관 찾기
        NursingInstitution nursingInstitution = nursingInstitutionRepository
                .findById(request.nursingInstitutionId())
                .orElseThrow(() -> new NursingInstitutionException(NURSING_INSTITUTION_NOT_FOUND));

        // 닉네임 중복 검사
        checkSameNickName(request.nickName());

        // 사용자 전화번호 중복 검사
        if (socialworkerRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new SocialWorkerException(SOCIALWORKER_ALREADY_EXISTS_PHONENUMBER);
        }

        LocalDate birthDate = parseBirthDate(request.birthYymmdd(), request.genderCode());
        Gender gender = parseGender(request.genderCode());

        // Socialworker 엔티티 생성
        SocialWorker socialworker = SocialWorker.create(
                request.realName(),
                request.nickName(),
                birthDate,
                gender,
                request.phoneNumber(),
                request.institutionRank(),
                AssociationRank.NONE,
                request.isAgreedToReceiveMarketingInfo(),
                nursingInstitution);

        socialworkerRepository.save(socialworker);

        updateJwtAndSecurityContext(
                httpServletResponse, request.phoneNumber(), request.institutionRank(), AssociationRank.NONE);

        return socialworker.getId();
    }

    @Transactional
    public ChatList getChatList() {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();
        NursingInstitution nursingInstitution = socialworker.getNursingInstitution();
        List<Matching> matchingList = matchingRepository.findByNursingInstitution(nursingInstitution);

        List<ChatList.ChatroomInfo> chatroomInfoList = matchingList.stream()
                .map(matching -> {
                    // Elderly와 Caregiver 정보를 가져오기
                    Caregiver caregiver = matching.getWorkApplication().getCaregiver();
                    Elderly elderly = matching.getRecruitment().getElderly();
                    String timeDifference = getTimeDifferenceString(matching);
                    Contract latestContract = contractRepository
                            .findTop1ByMatchingOrderByCreateDateDesc(matching)
                            .get();
                    String recentChat = isContractInCompletedMatching(matching) ? "최종 승인이 확정되었습니다!" : "합격 축하드립니다.";

                    // ChatroomInfo 생성
                    return new ChatList.ChatroomInfo(
                            matching.getId(),
                            caregiver.getProfileImageUrl(), // 어르신 프로필 이미지 URL
                            caregiver.getName(), // 요양보호자 이름
                            recentChat, // 최근 채팅
                            timeDifference,
                            elderly.getName(), // 어르신 이름
                            elderly.getAge(), // 어르신 나이
                            elderly.getGender() // 어르신 성별
                            );
                })
                .collect(Collectors.toList());

        // ChatList 반환
        return new ChatList(chatroomInfoList);
    }

    public LocalDateTime findLatestContractCreatedDate(Matching matching) {
        Contract latestContract = contractRepository
                .findTop1ByMatchingOrderByCreateDateDesc(matching)
                .orElse(null);
        return latestContract != null ? latestContract.getCreateDate() : null;
    }

    public boolean isContractInCompletedMatching(Matching matching) {
        Contract latestContract = contractRepository
                .findTop1ByMatchingOrderByCreateDateDesc(matching)
                .orElse(null);
        if (latestContract != null) {
            return completedMatchingRepository.existsCompletedMatchingByContract(latestContract);
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

    private void validateEssentialAgreement(boolean isAgreedToTerms, boolean isAgreedToCollectPersonalInfo) {
        if (isAgreedToTerms && isAgreedToCollectPersonalInfo) {
            return;
        }

        throw new SocialWorkerException(SOCIALWORKER_REQUIRED_AGREEMENT);
    }

    private void checkSameNickName(String nickName) {
        if (!socialworkerRepository.existsByNickname(nickName)) {
            return;
        }
        throw new SocialWorkerException(SOCIAlWORKER_ALREADY_EXISTS_NICKNAME);
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

    private void updateJwtAndSecurityContext(
            HttpServletResponse response,
            String phoneNumber,
            InstitutionRank institutionRankParam,
            AssociationRank associationRankParam) {
        String institutionRank = institutionRankParam.toString();
        String associationRank = associationRankParam.toString();
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

    public SocialWorkerMyInfo getMyInfo() {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        NursingInstitution nursingInstitution = loggedInSocialWorker.getNursingInstitution();
        Association association = loggedInSocialWorker.getAssociation();
        Integer age = Period.between(loggedInSocialWorker.getBirthday(), LocalDate.now())
                .getYears();
        return SocialWorkerMyInfo.of(loggedInSocialWorker, age, nursingInstitution, association);
    }

    public void updateMyBasicInfo(@Valid SocialWorkerUpdateBasicInfoRequest request, HttpServletResponse response) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();

        validateEssentialAgreement(request.isAgreedToTerms(), request.isAgreedToCollectPersonalInfo());

        // 기관ID로 기관 찾기
        NursingInstitution nursingInstitution = nursingInstitutionRepository
                .findById(request.nursingInstitutionId())
                .orElseThrow(() -> new NursingInstitutionException(NURSING_INSTITUTION_NOT_FOUND));

        // 닉네임 중복 검사
        if (!Objects.equals(request.nickName(), loggedInSocialWorker.getNickname())) {
            checkSameNickName(request.nickName());
        }

        // 사용자 전화번호 중복 검사
        if (!Objects.equals(loggedInSocialWorker.getPhoneNumber(), request.phoneNumber())
                && socialworkerRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new SocialWorkerException(SOCIALWORKER_ALREADY_EXISTS_PHONENUMBER);
        }

        LocalDate birthDate = parseBirthDate(request.birthYymmdd(), request.genderCode());
        Gender gender = parseGender(request.genderCode());

        boolean rankChanged = !Objects.equals(loggedInSocialWorker.getInstitutionRank(), request.institutionRank());
        boolean phoneChanged = !Objects.equals(loggedInSocialWorker.getPhoneNumber(), request.phoneNumber());

        if (rankChanged || phoneChanged) {
            updateJwtAndSecurityContext(
                    response,
                    request.phoneNumber(),
                    request.institutionRank(),
                    loggedInSocialWorker.getAssociationRank());
        }

        loggedInSocialWorker.updateBasicInfo(request, birthDate, gender, nursingInstitution);
    }
}
