package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.dto.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.dto.*;
import com.becareful.becarefulserver.domain.nursing_institution.dto.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import java.util.*;

public record ChatroomContentResponse(
        Long matchingId,
        Long recruitmentId,
        ElderlySimpleDto elderlyInfo,
        InstitutionSimpleDto institutionInfo,
        CaregiverSimpleDto caregiverInfo,
        CaregiverContractInfoDto caregiverContractInfo,
        List<ContractDto> contractList) {

    public static ChatroomContentResponse of(
            Matching matching,
            String caregiverName,
            Integer caregiverAge,
            String caregiverPhoneNumber,
            List<Contract> contractList) {
        Elderly elderly = matching.getRecruitment().getElderly();

        CaregiverSimpleDto caregiverDto = null;
        if (matching.getWorkApplication() != null) {
            caregiverDto = CaregiverSimpleDto.from(matching.getWorkApplication().getCaregiver());
        }

        return new ChatroomContentResponse(
                matching.getId(),
                matching.getRecruitment().getId(),
                ElderlySimpleDto.from(elderly),
                InstitutionSimpleDto.from(elderly.getNursingInstitution()),
                caregiverDto,
                CaregiverContractInfoDto.of(caregiverName, caregiverAge, caregiverPhoneNumber),
                contractList.stream().map(ContractDto::from).toList());
    }
}
