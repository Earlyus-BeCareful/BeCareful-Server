package com.becareful.becarefulserver.domain.matching.domain.converter;

import jakarta.persistence.Converter;

import com.becareful.becarefulserver.domain.caregiver.domain.converter.EnumSetConverter;
import com.becareful.becarefulserver.domain.matching.domain.MediationType;

@Converter
public class MediationTypeSetConverter extends EnumSetConverter<MediationType> {

    protected MediationTypeSetConverter() {
        super(MediationType.class);
    }
}
