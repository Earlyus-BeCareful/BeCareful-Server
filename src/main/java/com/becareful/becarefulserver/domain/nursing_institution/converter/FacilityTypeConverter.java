package com.becareful.becarefulserver.domain.nursing_institution.converter;

import com.becareful.becarefulserver.domain.caregiver.domain.converter.EnumSetConverter;
import com.becareful.becarefulserver.domain.nursing_institution.vo.FacilityType;
import jakarta.persistence.Converter;

@Converter
public class FacilityTypeConverter extends EnumSetConverter<FacilityType> {
    protected FacilityTypeConverter() {
        super(FacilityType.class);
    }
}
