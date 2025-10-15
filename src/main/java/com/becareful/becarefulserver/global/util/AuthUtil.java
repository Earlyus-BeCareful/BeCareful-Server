package com.becareful.becarefulserver.global.util;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.association.repository.AssociationMemberRepository;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.global.exception.exception.CaregiverException;
import com.becareful.becarefulserver.global.exception.exception.DomainException;
import com.becareful.becarefulserver.global.exception.exception.SocialWorkerException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final CaregiverRepository caregiverRepository;
    private final SocialWorkerRepository socialworkerRepository;
    private final AssociationMemberRepository associationMemberRepository;

    public Caregiver getLoggedInCaregiver() {
        String phoneNumber =
                SecurityContextHolder.getContext().getAuthentication().getName();
        return caregiverRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CaregiverException(CAREGIVER_NOT_EXISTS));
    }

    public SocialWorker getLoggedInSocialWorker() {
        String phoneNumber =
                SecurityContextHolder.getContext().getAuthentication().getName();
        return socialworkerRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new SocialWorkerException(SOCIALWORKER_NOT_EXISTS));
    }

    public AssociationMember getLoggedInAssociationMember() {
        String phoneNumber =
                SecurityContextHolder.getContext().getAuthentication().getName();
        return associationMemberRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new DomainException(ASSOCIATION_MEMBER_NOT_EXISTS));
    }
}
