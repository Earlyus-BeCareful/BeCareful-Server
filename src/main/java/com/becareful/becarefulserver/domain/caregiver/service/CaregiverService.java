package com.becareful.becarefulserver.domain.caregiver.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.CAREGIVER_REQUIRED_AGREEMENT;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.dto.request.CaregiverCreateRequest;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.global.exception.ErrorMessage;
import com.becareful.becarefulserver.global.exception.exception.CaregiverException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CaregiverService {

    private final CaregiverRepository caregiverRepository;

    @Transactional
    public Long saveCaregiver(CaregiverCreateRequest request) {

        validateTermAgreement(request.isAgreedToTerms(), request.isAgreedToCollectPersonalInfo());
        Caregiver caregiver = Caregiver.create(
                request.name(), request.phoneNumber(), getEncodedPassword(request.password()),
                request.gender(), request.streetAddress(), request.detailAddress(),
                request.isHavingCar(), request.isCompleteDementiaEducation(),
                request.isAgreedToReceiveMarketingInfo(),
                request.profileImageUrl());
        caregiverRepository.save(caregiver);
        return caregiver.getId();
    }

    private void validateTermAgreement(boolean isAgreedToTerms,
            boolean isAgreedToCollectPersonalInfo) {
        if (isAgreedToTerms && isAgreedToCollectPersonalInfo) {
            return;
        }

        throw new CaregiverException(CAREGIVER_REQUIRED_AGREEMENT);
    }

    private String getEncodedPassword(String rawPassword) {
        var passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(rawPassword);
    }
}
