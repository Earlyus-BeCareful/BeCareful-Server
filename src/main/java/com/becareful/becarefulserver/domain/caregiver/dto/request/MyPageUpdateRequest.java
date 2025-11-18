package com.becareful.becarefulserver.domain.caregiver.dto.request;

import com.becareful.becarefulserver.domain.caregiver.domain.vo.Address;
import com.becareful.becarefulserver.domain.caregiver.domain.vo.Certificate;
import jakarta.validation.constraints.NotNull;

public record MyPageUpdateRequest(
        @NotNull String phoneNumber,
        String profileImageTempKey,
        @NotNull Certificate caregiverCertificate,
        Certificate socialWorkerCertificate,
        Certificate nursingCareCertificate,
        Address address,
        boolean isHavingCar,
        boolean isCompleteDementiaEducation) {}
