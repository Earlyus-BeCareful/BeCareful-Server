package com.becareful.becarefulserver.domain.recruitment.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.ELDERLY_NOT_EXISTS;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplicationWorkLocation;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationWorkLocationRepository;
import com.becareful.becarefulserver.domain.recruitment.domain.Matching;
import com.becareful.becarefulserver.domain.recruitment.domain.Recruitment;
import com.becareful.becarefulserver.domain.recruitment.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.recruitment.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.recruitment.repository.RecruitmentRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.global.exception.exception.RecruitmentException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final WorkApplicationWorkLocationRepository workApplicationWorkLocationRepository;
    private final ElderlyRepository elderlyRepository;
    private final MatchingRepository matchingRepository;

    @Transactional
    public void createRecruitment(RecruitmentCreateRequest request) {
        Elderly elderly = elderlyRepository.findById(request.elderlyId())
                .orElseThrow((() -> new RecruitmentException(ELDERLY_NOT_EXISTS)));

        Recruitment recruitment = Recruitment.create(request, elderly);
        recruitmentRepository.save(recruitment);

        matchingWith(recruitment);
    }

    private void matchingWith(Recruitment recruitment) {
        workApplicationWorkLocationRepository.findAllActiveWorkApplication().stream()
                .filter((application -> isMatched(recruitment, application)))
                .forEach(wa ->
                    matchingRepository.save(Matching.create(recruitment, wa.getWorkApplication())));
    }

    private boolean isMatched(Recruitment recruitment, WorkApplicationWorkLocation workApplicationWorkLocation) {
        // TODO : 테스트 데이터 제대로 넣기
        return true;
    }
}
