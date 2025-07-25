package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.vo.FacilityType;
import java.util.EnumSet;

public class NursingInstitutionFixture {

    public static NursingInstitution NURSING_INSTITUTION = create();

    public static NursingInstitution create() {
        return NursingInstitution.create(
                "행복요양기관", "code", 2024, EnumSet.noneOf(FacilityType.class), "031-123-1234", "도로명주소", "상세주소", "url");
    }
}
