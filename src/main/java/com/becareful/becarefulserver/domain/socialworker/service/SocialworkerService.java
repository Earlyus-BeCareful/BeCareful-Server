package com.becareful.becarefulserver.domain.socialworker.service;

import com.becareful.becarefulserver.domain.socialworker.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.Socialworker;
import com.becareful.becarefulserver.domain.socialworker.dto.request.SocialworkerCreateRequest;
import com.becareful.becarefulserver.domain.socialworker.repository.NursingInstitutionRepository;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialworkerRepository;
import com.becareful.becarefulserver.global.exception.exception.NursingInstitutionException;
import com.becareful.becarefulserver.global.exception.exception.SocialworkerException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class SocialworkerService {
    private final SocialworkerRepository socialworkerRepository;
    private final NursingInstitutionRepository nursingInstitutionRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public Long saveSocialworker(SocialworkerCreateRequest request) {

        validateEssentialAgreement(request.isAgreedToTerms(), request.isAgreedToCollectPersonalInfo());

        NursingInstitution institution = nursingInstitutionRepository.findById(request.institutionId())
                .orElseThrow(() -> new NursingInstitutionException(NURSING_INSTITUTION_NOT_FOUND));

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