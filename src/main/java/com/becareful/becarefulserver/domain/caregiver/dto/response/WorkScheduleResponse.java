package com.becareful.becarefulserver.domain.caregiver.dto.response;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.vo.Gender;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record WorkScheduleResponse(
        String workStartTime,
        String workEndTime,
        String seniorName,
        Gender seniorGender,
        Integer seniorAge,
        @Schema(description = "더미 형식이므로 변경될 수 있습니다.")
        List<CareType> seniorCareType,
        @Schema(description = "더미 형식이므로 변경될 수 있습니다.")
        String workLocation
) {
    public static WorkScheduleResponse createDummy() {
        return WorkScheduleResponse.builder()
                .workStartTime(LocalTime.of(8, 20).format(DateTimeFormatter.ofPattern("HH:mm")))
                .workEndTime(LocalTime.of(17, 20).format(DateTimeFormatter.ofPattern("HH:mm")))
                .seniorName("박순자")
                .seniorGender(Gender.FEMALE)
                .seniorAge(65)
                .seniorCareType(List.of(CareType.식사보조, CareType.이동보조))
                .workLocation("노원구 상계동")
                .build();
    }
}
