package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryUnitType;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.caregiver.dto.request.WorkApplicationCreateOrUpdateRequest;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.common.domain.vo.Location;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.DayOfWeek;
import java.util.List;

public class WorkApplicationFixture {
    public WorkApplication createApplicantFixture(Caregiver caregiver) {
        WorkApplicationCreateOrUpdateRequest request = new WorkApplicationCreateOrUpdateRequest(
                List.of(Location.of("서울특별시", "마포구", "상수동")),
                List.of(DayOfWeek.MONDAY),
                List.of(WorkTime.MORNING, WorkTime.AFTERNOON,WorkTime.AFTERNOON),
                List.of(CareType.식사보조),
                WorkSalaryUnitType.HOUR,
                10000);
        return WorkApplication.create(request, caregiver);
    }
}