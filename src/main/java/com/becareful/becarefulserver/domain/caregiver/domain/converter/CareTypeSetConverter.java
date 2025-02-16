package com.becareful.becarefulserver.domain.caregiver.domain.converter;

import jakarta.persistence.Converter;

import com.becareful.becarefulserver.domain.common.domain.CareType;

@Converter
public class CareTypeSetConverter extends EnumSetConverter<CareType> {

    protected CareTypeSetConverter() {
        super(CareType.class);
    }
}
