package com.becareful.becarefulserver.domain.caregiver.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.dto.request.CaregiverCreateRequest;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CaregiverService {

    private final CaregiverRepository caregiverRepository;

    @Transactional
    public Long saveCaregiver(CaregiverCreateRequest request) {
        Caregiver caregiver = request.toEntity();
        caregiverRepository.save(caregiver);
        return caregiver.getId();
    }
}
