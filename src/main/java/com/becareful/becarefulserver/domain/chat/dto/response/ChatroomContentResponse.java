package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.dto.ContractDto;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import java.util.List;

public record ChatroomContentResponse(
        ElderlySimpleDto elderlyInfo,
        String nursingInstitutionName,
        String careGiverName,
        List<ContractDto> contractInfoResponseList) {

    public static ChatroomContentResponse of(Matching matching, List<Contract> contractList) {
        return new ChatroomContentResponse(
                ElderlySimpleDto.from(matching.getRecruitment().getElderly()),
                matching.getRecruitment().getElderly().getNursingInstitution().getName(),
                matching.getWorkApplication().getCaregiver().getName(),
                contractList.stream().map(ContractDto::from).toList());
    }
}
