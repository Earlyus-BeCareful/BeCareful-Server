package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.chat.domain.Contract;
import com.becareful.becarefulserver.domain.chat.dto.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.dto.*;
import com.becareful.becarefulserver.domain.nursing_institution.dto.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import java.util.*;

public record ChatRoomDetailResponse(
        Long matchingId, //TODO: 필수 정보인가??
        Long recruitmentId,
        ElderlySimpleDto elderlyInfo,
        InstitutionSimpleDto institutionInfo,
        CaregiverSimpleDto caregiverInfo,
        CaregiverContractInfoDto caregiverContractInfo,
        List<ContractDto> contractList) {

    public static ChatRoomDetailResponse of(Matching matching, List<Contract> contractList) {
        Elderly elderly = matching.getRecruitment().getElderly();
        Caregiver caregiver = matching.getWorkApplication().getCaregiver();

        return new ChatRoomDetailResponse(
                matching.getId(),
                matching.getRecruitment()
                        .getId(), // TODO : recruitment id, elderly info, institution info 를 recruitment info 로 통합
                ElderlySimpleDto.from(elderly),
                InstitutionSimpleDto.from(elderly.getNursingInstitution()),
                matching.getWorkApplication() != null ? CaregiverSimpleDto.from(caregiver) : null,
                CaregiverContractInfoDto.from(caregiver), // TODO : caregvier simple dto 와 통합
                contractList.stream().map(ContractDto::from).toList());
    }
}
