package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.common.domain.vo.Location;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.CareLevel;
import java.time.LocalDate;
import java.util.EnumSet;

public class ElderlyFixture {

    public static Elderly create(String name) {
        return Elderly.create(
                name,
                LocalDate.of(1940, 1, 1),
                Gender.FEMALE,
                Location.of("서울시", "마포구", "상수동"),
                "홍익대학교",
                false,
                false,
                "image url",
                NursingInstitutionFixture.NURSING_INSTITUTION,
                CareLevel.일등급,
                "condition",
                EnumSet.of(DetailCareType.스스로식사가능));
    }
}
