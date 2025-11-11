package com.becareful.becarefulserver.domain.socialworker.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.association.domain.*;
import com.becareful.becarefulserver.domain.association.repository.AssociationMemberRepository;
import com.becareful.becarefulserver.domain.chat.repository.SocialWorkerChatReadStatusRepository;
import com.becareful.becarefulserver.domain.common.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.*;
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
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
public class SocialWorkerService {

    private final SocialWorkerRepository socialworkerRepository;
    private final NursingInstitutionRepository nursingInstitutionRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final ElderlyRepository elderlyRepository;
    private final SocialWorkerChatReadStatusRepository socialWorkerChatReadStatusRepository;
    private final AuthUtil authUtil;
    private final CookieUtil cookieUtil;
    private final AssociationMemberRepository associationMemberRepository;

    @Transactional
    public Long createSocialWorker(SocialWorkerCreateRequest request) {
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
                request.isAgreedToReceiveMarketingInfo(),
                nursingInstitution);

        socialworkerRepository.save(socialWorker);

        return socialWorker.getId();
    }

    @Transactional(readOnly = true)
    public SocialWorkerHomeResponse getHomeData() {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        NursingInstitution institution = loggedInSocialWorker.getNursingInstitution();

        List<Elderly> institutionElderlys = elderlyRepository.findAllByNursingInstitution(institution);

        List<SocialWorkerSimpleDto> institutionSocialWorkers =
                socialworkerRepository.findAllByNursingInstitution(institution).stream()
                        .map(SocialWorkerSimpleDto::from)
                        .toList();

        List<Recruitment> recruitments = recruitmentRepository.findAllByElderlyIn(institutionElderlys);

        boolean hasNewChat = socialWorkerChatReadStatusRepository.existsUnreadContract(loggedInSocialWorker);

        int institutionElderlyCount = institutionElderlys.size();

        return SocialWorkerHomeResponse.of(
                loggedInSocialWorker, institutionSocialWorkers, recruitments, institutionElderlyCount, hasNewChat);
    }

    @Transactional(readOnly = true)
    public boolean checkSameNickNameAtRegist(String nickName) {
        return socialworkerRepository.existsByNickname(nickName);
    }

    @Transactional(readOnly = true)
    public SocialWorkerMyPageResponse getMyPageData() {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        return SocialWorkerMyPageResponse.from(loggedInSocialWorker);
    }

    @Transactional(readOnly = true)
    public SocialWorkerDto getMyProfile() {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        return SocialWorkerDto.from(loggedInSocialWorker);
    }

    @Transactional
    public void updateSocialWorkerProfile(@Valid SocialWorkerProfileUpdateRequest request) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();

        validateEssentialAgreement(request.isAgreedToTerms(), request.isAgreedToCollectPersonalInfo());

        // 기관ID로 기관 찾기
        NursingInstitution institution = nursingInstitutionRepository
                .findById(request.nursingInstitutionId())
                .orElseThrow(() -> new NursingInstitutionException(NURSING_INSTITUTION_NOT_FOUND));

        boolean isNicknameChanged = !Objects.equals(request.nickName(), loggedInSocialWorker.getNickname());

        if (isNicknameChanged) {
            validateNicknameNotDuplicated(request.nickName());
        }

        LocalDate birthDate = parseBirthDate(request.birthYymmdd(), request.genderCode());
        Gender gender = Gender.fromGenderCode(request.genderCode());

        loggedInSocialWorker.update(request, birthDate, gender, institution);
    }

    @Transactional
    public void deleteSocialWorker(HttpServletResponse response) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        AssociationMember associationMember = loggedInSocialWorker.getAssociationMember();

        if (associationMember != null) {
            AssociationRank rank = associationMember.getAssociationRank();
            Association association = associationMember.getAssociation();

            if (rank == AssociationRank.CHAIRMAN) {
                throw new AssociationException(ASSOCIATION_CHAIRMAN_SELECT_SUCCESSOR_FIRST);
            }

            if (rank == AssociationRank.EXECUTIVE
                    & associationMemberRepository.countByAssociationAndAssociationRank(
                                    association, AssociationRank.EXECUTIVE)
                            == 1) {
                throw new AssociationException(ASSOCIATION_EXECUTIVE_SELECT_SUCCESSOR_FIRST);
            }
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
}
