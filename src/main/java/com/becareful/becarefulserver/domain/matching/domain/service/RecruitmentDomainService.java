package com.becareful.becarefulserver.domain.matching.domain.service;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.global.exception.exception.RecruitmentException;
import org.springframework.stereotype.Component;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.RECRUITMENT_DIFFERENT_INSTITUTION;

@Component
public class RecruitmentDomainService {

    public void validateRecruitmentInstitution(Recruitment recruitment, SocialWorker socialWorker) {
        if (!recruitment.getElderly().getNursingInstitution().equals(socialWorker.getNursingInstitution())) {
            throw new RecruitmentException(RECRUITMENT_DIFFERENT_INSTITUTION);
        }
    }
}
