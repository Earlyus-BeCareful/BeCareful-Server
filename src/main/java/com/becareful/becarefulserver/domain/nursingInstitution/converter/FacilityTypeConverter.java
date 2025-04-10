package com.becareful.becarefulserver.domain.nursingInstitution.converter;

import com.becareful.becarefulserver.domain.caregiver.domain.converter.EnumSetConverter;
import com.becareful.becarefulserver.domain.nursingInstitution.vo.FacilityType;
import jakarta.persistence.Converter;

@Converter
public class FacilityTypeConverter extends EnumSetConverter<FacilityType> {
    protected FacilityTypeConverter() {
        super(FacilityType.class);
    }
}
