package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.dto.ContractDto;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import java.util.List;

public record ChatroomContentResponse(
        String elderlyName,
        String nursingInstitutionName,
        String careGiverName,
        List<ContractDto> contractInfoResponseList) {

    public static ChatroomContentResponse of(Matching matching, List<Contract> contractList) {
        return new ChatroomContentResponse(
                matching.getRecruitment().getElderly().getName(),
                matching.getRecruitment().getElderly().getNursingInstitution().getName(),
                matching.getWorkApplication().getCaregiver().getName(),
                contractList.stream().map(ContractDto::from).toList());
    }
}
