package com.becareful.becarefulserver.domain.matching.domain.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.global.exception.exception.DomainException;
import com.becareful.becarefulserver.global.exception.exception.RecruitmentException;
import org.springframework.stereotype.Component;

@Component
public class RecruitmentDomainService {

    public void validateRecruitmentInstitution(Recruitment recruitment, SocialWorker socialWorker) {
        if (!recruitment.getElderly().getNursingInstitution().equals(socialWorker.getNursingInstitution())) {
            throw new RecruitmentException(RECRUITMENT_DIFFERENT_INSTITUTION);
        }
    }

    public void validateRecruitmentUpdatable(Recruitment recruitment, boolean isApplicantOrProcessingContractExists) {
        if (!recruitment.getRecruitmentStatus().isRecruiting()) {
            throw new DomainException(RECRUITMENT_NOT_UPDATABLE_NOT_RECRUITING);
        }

        if (isApplicantOrProcessingContractExists) {
            throw new DomainException(RECRUITMENT_NOT_UPDATABLE_APPLICANTS_OR_PROCESSING_CONTRACT_EXISTS);
        }
    }

    public void validateRecruitmentDeletable(Recruitment recruitment, boolean isApplicantOrProcessingContractExists) {
        if (!recruitment.getRecruitmentStatus().isRecruiting()) {
            throw new DomainException(RECRUITMENT_NOT_DELETABLE_NOT_RECRUITING);
        }

        if (isApplicantOrProcessingContractExists) {
            throw new DomainException(RECRUITMENT_NOT_DELETABLE_APPLICANTS_OR_PROCESSING_CONTRACT_EXISTS);
        }
    }
}
