package com.becareful.becarefulserver.domain.socialworker.service;

import com.becareful.becarefulserver.domain.recruitment.domain.Matching;
import com.becareful.becarefulserver.domain.recruitment.domain.MatchingStatus;
import com.becareful.becarefulserver.domain.recruitment.domain.Recruitment;
import com.becareful.becarefulserver.domain.recruitment.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.Socialworker;
import com.becareful.becarefulserver.domain.socialworker.dto.request.SocialworkerCreateRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.response.SimpleElderlyResponse;
import com.becareful.becarefulserver.domain.socialworker.dto.response.SocialWorkerHomeResponse;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.domain.socialworker.repository.NursingInstitutionRepository;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialworkerRepository;
import com.becareful.becarefulserver.global.exception.exception.NursingInstitutionException;
import com.becareful.becarefulserver.global.exception.exception.SocialworkerException;
import com.becareful.becarefulserver.global.util.AuthUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialworkerService {
    private final SocialworkerRepository socialworkerRepository;
    private final NursingInstitutionRepository nursingInstitutionRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthUtil authUtil;
    private final ElderlyRepository elderlyRepository;
    private final MatchingRepository matchingRepository;

    public SocialWorkerHomeResponse getHomeData() {
        Socialworker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        Integer elderlyCount = elderlyRepository.findByNursingInstitution(loggedInSocialWorker.getNursingInstitution()).size();
        Integer socialWorkerCount = socialworkerRepository.countByNursingInstitution(loggedInSocialWorker.getNursingInstitution());

        List<Long> elderlyIds = elderlyRepository.findByNursingInstitution(loggedInSocialWorker.getNursingInstitution()).stream()
                .map(Elderly::getId)
                .toList();

        List<Matching> matchingList = matchingRepository.findAllMatchingByElderlyIds(elderlyIds);

        Long processingMatchingCount = matchingList.stream()
                .filter(matching -> matching.getMatchingStatus().equals(MatchingStatus.지원))
                .count();

        Long recentlyMatchedCount = matchingList.stream()
                .filter(matching -> matching.getUpdateDate().isAfter(LocalDateTime.now().minusDays(7)))
                .filter(matching -> matching.getMatchingStatus().equals(MatchingStatus.합격))
                .count();

        Integer totalMatchedCount = matchingList.size();

        Integer appliedCaregiverCount = matchingList.stream()
                .filter(matching -> matching.getMatchingStatus().equals(MatchingStatus.지원))
                .map(matching -> matching.getWorkApplication().getId())
                .collect(Collectors.toSet())
                .size();

        long completedRecruitmentCount = matchingList.stream()
                .map(Matching::getRecruitment)
                .filter(recruitment -> !recruitment.isRecruiting())
                .count();

        long wholeApplierCountForCompletedRecruitment = matchingList.stream()
                .filter(matching -> !matching.getRecruitment().isRecruiting())
                .filter(matching -> matching.getMatchingStatus().equals(MatchingStatus.합격) || matching.getMatchingStatus().equals(MatchingStatus.불합격))
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

        return SocialWorkerHomeResponse.of(loggedInSocialWorker,
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

    @Transactional
    public Long saveSocialworker(SocialworkerCreateRequest request) {

        validateEssentialAgreement(request.isAgreedToTerms(), request.isAgreedToCollectPersonalInfo());

        NursingInstitution institution = nursingInstitutionRepository.findById(request.institutionId())
                .orElseThrow(() -> new NursingInstitutionException(NURSING_INSTITUTION_NOT_FOUND));

        if (socialworkerRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new SocialworkerException(SOCIALWORKER_ALREADY_EXISTS);
        }

        // Socialworker 엔티티 생성
        Socialworker socialworker = Socialworker.create(
                request.name(), request.gender(), request.phoneNumber(),
                getEncodedPassword(request.password()),
                institution,
                request.rank(),
                request.isAgreedToReceiveMarketingInfo()
        );

        socialworkerRepository.save(socialworker);

        return socialworker.getId();
    }

    private String getEncodedPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private void validateEssentialAgreement(boolean isAgreedToTerms,
                                            boolean isAgreedToCollectPersonalInfo) {
        if (isAgreedToTerms && isAgreedToCollectPersonalInfo) {
            return;
        }

        throw new SocialworkerException(SOCIALWORKER_REQUIRED_AGREEMENT);
    }
}