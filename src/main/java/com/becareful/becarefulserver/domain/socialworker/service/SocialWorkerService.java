package com.becareful.becarefulserver.domain.socialworker.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.association.domain.*;
import com.becareful.becarefulserver.domain.association.repository.*;
import com.becareful.becarefulserver.domain.chat.service.*;
import com.becareful.becarefulserver.domain.common.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.dto.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.*;
import com.becareful.becarefulserver.domain.nursing_institution.repository.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.*;
import com.becareful.becarefulserver.domain.socialworker.dto.*;
import com.becareful.becarefulserver.domain.socialworker.dto.request.*;
import com.becareful.becarefulserver.domain.socialworker.dto.response.*;
import com.becareful.becarefulserver.domain.socialworker.repository.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import com.becareful.becarefulserver.global.properties.*;
import com.becareful.becarefulserver.global.util.*;
import jakarta.servlet.http.*;
import jakarta.validation.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialWorkerService {
    private final SocialWorkerRepository socialworkerRepository;
    private final NursingInstitutionRepository nursingInstitutionRepository;

    private final MatchingRepository matchingRepository;
    private final ElderlyRepository elderlyRepository;

    private final AuthUtil authUtil;
    private final JwtUtil jwtUtil;
    private final CookieProperties cookieProperties;
    private final JwtProperties jwtProperties;
    private final CookieUtil cookieUtil;
    private final AssociationRepository associationRepository;
    private final SocialWorkerChatService socialWorkerChatService;

    @Transactional
    public Long createSocialWorker(SocialWorkerCreateRequest request, HttpServletResponse httpServletResponse) {
        validateEssentialAgreement(request.isAgreedToTerms(), request.isAgreedToCollectPersonalInfo());

        NursingInstitution nursingInstitution = nursingInstitutionRepository
                .findById(request.nursingInstitutionId())
                .orElseThrow(() -> new NursingInstitutionException(NURSING_INSTITUTION_NOT_FOUND));

        validateNicknameNotDuplicated(request.nickName());
        validatePhoneNumberNotDuplicated(request.phoneNumber());

        LocalDate birthDate = parseBirthDate(request.birthYymmdd(), request.genderCode());
        Gender gender = Gender.fromGenderCode(request.genderCode());

        SocialWorker socialWorker = SocialWorker.create(
                request.realName(),
                request.nickName(),
                birthDate,
                gender,
                request.phoneNumber(),
                request.institutionRank(),
                AssociationRank.NONE,
                request.isAgreedToReceiveMarketingInfo(),
                nursingInstitution);

        socialworkerRepository.save(socialWorker);

        updateJwtAndSecurityContext(
                httpServletResponse, request.phoneNumber(), request.institutionRank(), AssociationRank.NONE);

        return socialWorker.getId();
    }

    public SocialWorkerHomeResponse getHomeData() {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        NursingInstitution institution = loggedInSocialWorker.getNursingInstitution();

        boolean hasNewChat = socialWorkerChatService.checkNewChat();

        List<Long> elderlyIds = elderlyRepository.findByNursingInstitution(institution).stream()
                .map(Elderly::getId)
                .toList();

        List<SocialWorkerSimpleDto> socialWorkers =
                socialworkerRepository.findAllByNursingInstitution(institution).stream()
                        .map(SocialWorkerSimpleDto::from)
                        .toList();

        List<Matching> matchingList = matchingRepository.findAllByElderlyIds(elderlyIds);

        int elderlyCount = elderlyIds.size();
        int socialWorkerCount = socialWorkers.size();
        int totalMatchedCount = matchingList.size();

        int reviewingMatchingCount = 0;
        int recentlyMatchedCount = 0;
        int wholeCompletedMatchingCount = 0;
        int wholeApplierCountForCompletedRecruitment = 0;
        Set<Long> workApplicationIds = new HashSet<>();

        for (Matching matching : matchingList) {
            if (matching.isApplicationReviewing()) {
                reviewingMatchingCount++;
                workApplicationIds.add(matching.getWorkApplication().getId());
            } else if (matching.isApplicationPassed()) {
                if (matching.getUpdateDate().isAfter(LocalDateTime.now().minusDays(7))) {
                    recentlyMatchedCount++;
                }
            }

            if (!matching.getRecruitment().isRecruiting()) {
                wholeCompletedMatchingCount++;
                if (matching.isApplicationPassed() || matching.isApplicationRefused()) {
                    wholeApplierCountForCompletedRecruitment++;
                }
            }
        }

        int appliedCaregiverCount = workApplicationIds.size();

        List<ElderlySimpleDto> elderlyList = matchingList.stream()
                .map(Matching::getRecruitment)
                .filter(Recruitment::isRecruiting)
                .map(Recruitment::getElderly)
                .map(ElderlySimpleDto::from)
                .toList();

        return SocialWorkerHomeResponse.of(
                loggedInSocialWorker,
                hasNewChat,
                elderlyCount,
                socialWorkerCount, // TODO 요양보호사 숫자로 변경
                socialWorkers,
                reviewingMatchingCount,
                recentlyMatchedCount,
                totalMatchedCount,
                appliedCaregiverCount,
                wholeCompletedMatchingCount == 0
                        ? 0
                        : (double) wholeApplierCountForCompletedRecruitment / wholeCompletedMatchingCount,
                wholeCompletedMatchingCount == 0
                        ? 0
                        : ((double) wholeApplierCountForCompletedRecruitment / wholeCompletedMatchingCount) * 100,
                elderlyList);
    }

    public boolean checkSameNickNameAtRegist(String nickName) {
        return socialworkerRepository.existsByNickname(nickName);
    }

    public SocialWorkerMyResponse getMyInfo() {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        return SocialWorkerMyResponse.from(loggedInSocialWorker);
    }

    public SocialWorkerEditResponse getEditMyInfo() {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        return SocialWorkerEditResponse.from(loggedInSocialWorker);
    }

    @Transactional
    public void updateMyBasicInfo(@Valid SocialWorkerUpdateBasicInfoRequest request, HttpServletResponse response) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();

        validateEssentialAgreement(request.isAgreedToTerms(), request.isAgreedToCollectPersonalInfo());

        // 기관ID로 기관 찾기
        NursingInstitution nursingInstitution = nursingInstitutionRepository
                .findById(request.nursingInstitutionId())
                .orElseThrow(() -> new NursingInstitutionException(NURSING_INSTITUTION_NOT_FOUND));

        // 닉네임 중복 검사
        if (!Objects.equals(request.nickName(), loggedInSocialWorker.getNickname())) {
            validateNicknameNotDuplicated(request.nickName());
        }

        // 사용자 전화번호 중복 검사
        if (!Objects.equals(loggedInSocialWorker.getPhoneNumber(), request.phoneNumber())
                && socialworkerRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new SocialWorkerException(SOCIALWORKER_ALREADY_EXISTS_PHONENUMBER);
        }

        LocalDate birthDate = parseBirthDate(request.birthYymmdd(), request.genderCode());
        Gender gender = Gender.fromGenderCode(request.genderCode());

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

    public void logout(HttpServletResponse response) {
        // AccessToken 쿠키 삭제
        response.addCookie(cookieUtil.deleteCookie("AccessToken"));
        // RefreshToken 쿠키 삭제
        response.addCookie(cookieUtil.deleteCookie("RefreshToken"));
        // SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }

    @Transactional
    public void leave(HttpServletResponse response) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        AssociationRank rank = loggedInSocialWorker.getAssociationRank();
        Association association = loggedInSocialWorker.getAssociation();

        if (rank == AssociationRank.CHAIRMAN) {
            throw new AssociationException(ASSOCIATION_CHAIRMAN_SELECT_SUCCESSOR_FIRST);
        }

        if (rank == AssociationRank.EXECUTIVE
                & socialworkerRepository.countByAssociationAndAssociationRank(association, AssociationRank.EXECUTIVE)
                        == 1) {
            throw new AssociationException(ASSOCIATION_EXECUTIVE_SELECT_SUCCESSOR_FIRST);
        }

        socialworkerRepository.delete(loggedInSocialWorker);

        response.addCookie(cookieUtil.deleteCookie("AccessToken"));
        response.addCookie(cookieUtil.deleteCookie("RefreshToken"));

        SecurityContextHolder.clearContext();
    }

    private void validateEssentialAgreement(boolean isAgreedToTerms, boolean isAgreedToCollectPersonalInfo) {
        if (isAgreedToTerms && isAgreedToCollectPersonalInfo) {
            return;
        }

        throw new SocialWorkerException(SOCIALWORKER_REQUIRED_AGREEMENT);
    }

    private void validateNicknameNotDuplicated(String nickName) {
        if (socialworkerRepository.existsByNickname(nickName)) {
            throw new SocialWorkerException(SOCIAlWORKER_ALREADY_EXISTS_NICKNAME);
        }
    }

    private void validatePhoneNumberNotDuplicated(String phoneNumber) {
        if (socialworkerRepository.existsByPhoneNumber(phoneNumber)) {
            throw new SocialWorkerException(SOCIALWORKER_ALREADY_EXISTS_PHONENUMBER);
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

    private void updateJwtAndSecurityContext(
            HttpServletResponse response,
            String phoneNumber,
            InstitutionRank institutionRankParam,
            AssociationRank associationRankParam) {
        String institutionRank = institutionRankParam.toString();
        String associationRank = associationRankParam.toString();
        String accessToken = jwtUtil.createAccessToken(phoneNumber, institutionRank, associationRank);
        String refreshToken = jwtUtil.createRefreshToken(phoneNumber);

        response.addCookie(cookieUtil.createCookie("AccessToken", accessToken, jwtProperties.getAccessTokenExpiry()));
        response.addCookie(
                cookieUtil.createCookie("RefreshToken", refreshToken, jwtProperties.getRefreshTokenExpiry()));

        List<GrantedAuthority> authorities =
                List.of((GrantedAuthority) () -> institutionRank, (GrantedAuthority) () -> associationRank);

        Authentication auth = new UsernamePasswordAuthenticationToken(phoneNumber, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
