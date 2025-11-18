package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryUnitType;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class RecruitmentFixture {

    public static Recruitment createRecruitment(String title, Elderly elderly) {
        RecruitmentCreateRequest request = new RecruitmentCreateRequest(
                elderly.getId(),
                title,
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                List.of(CareType.식사보조),
                WorkSalaryUnitType.HOUR,
                15000,
                "description");
        return Recruitment.create(request, elderly);
    }
}
