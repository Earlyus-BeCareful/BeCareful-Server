package com.becareful.becarefulserver.domain.recruitment.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.ELDERLY_NOT_EXISTS;

import org.springframework.stereotype.Service;

import com.becareful.becarefulserver.domain.recruitment.domain.Recruitment;
import com.becareful.becarefulserver.domain.recruitment.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.recruitment.repository.RecruitmentRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.global.exception.exception.RecruitmentException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final ElderlyRepository elderlyRepository;

    public void createRecruitment(RecruitmentCreateRequest request) {
        Elderly elderly = elderlyRepository.findById(request.elderlyId())
                .orElseThrow((() -> new RecruitmentException(ELDERLY_NOT_EXISTS)));

        Recruitment recruitment = Recruitment.create(request, elderly);
        recruitmentRepository.save(recruitment);
    }
}
