package com.becareful.becarefulserver.domain.caregiver.domain.converter;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import jakarta.persistence.Converter;

@Converter
public class CareTypeSetConverter extends EnumSetConverter<CareType> {

    protected CareTypeSetConverter() {
        super(CareType.class);
    }
}
