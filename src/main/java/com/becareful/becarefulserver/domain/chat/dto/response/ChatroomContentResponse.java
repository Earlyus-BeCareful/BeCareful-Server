package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.dto.ContractDto;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.dto.CaregiverSimpleDto;
import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import com.becareful.becarefulserver.domain.matching.dto.InstitutionSimpleDto;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import java.util.List;

public record ChatroomContentResponse(
        ElderlySimpleDto elderlyInfo,
        InstitutionSimpleDto institutionInfo,
        CaregiverSimpleDto caregiverInfo,
        List<ContractDto> contractList) {

    public static ChatroomContentResponse of(Matching matching, List<Contract> contractList) {
        Elderly elderly = matching.getRecruitment().getElderly();
        return new ChatroomContentResponse(
                ElderlySimpleDto.from(elderly),
                InstitutionSimpleDto.from(elderly.getNursingInstitution()),
                CaregiverSimpleDto.from(matching.getWorkApplication().getCaregiver()),
                contractList.stream().map(ContractDto::from).toList());
    }
}
